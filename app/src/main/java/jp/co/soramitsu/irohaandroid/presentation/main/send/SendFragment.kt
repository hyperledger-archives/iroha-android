package jp.co.soramitsu.irohaandroid.presentation.main.send

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.google.zxing.integration.android.IntentIntegrator
import com.jakewharton.rxbinding2.view.RxView
import com.tbruyelle.rxpermissions2.RxPermissions
import jp.co.soramitsu.irohaandroid.IrohaSampleApp
import jp.co.soramitsu.irohaandroid.R
import jp.co.soramitsu.irohaandroid.presentation.base.BaseFragment
import jp.co.soramitsu.irohaandroid.presentation.base.ChooserDialog
import jp.co.soramitsu.irohaandroid.presentation.main.MainViewModel
import jp.co.soramitsu.irohaandroid.util.username
import kotlinx.android.synthetic.main.fragment_send.*

class SendFragment : BaseFragment<MainViewModel>() {

    companion object {
        private const val PICK_IMAGE_REQUEST = 101

        fun newInstance(): SendFragment {
            return SendFragment()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_send, container, false)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents == null) {
                Toast.makeText(activity, R.string.scan_canceled, Toast.LENGTH_LONG).show()
            } else {
                viewModel.qrStringProcess(result.contents)
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            viewModel.decodeTextFromBitmapQr(data.data!!)
        }
    }

    private lateinit var chooserDialog: ChooserDialog
    private lateinit var integrator: IntentIntegrator

    override fun initViews() {
        send.setOnClickListener { viewModel.transfer(to.text.toString(), amount.text.toString()) }

        qr.setOnClickListener { showQrChooser() }

        integrator = IntentIntegrator.forSupportFragment(this).apply {
            setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
            setPrompt(getString(R.string.scan_qr))
            setOrientationLocked(true)
            setBeepEnabled(false)
        }

        chooserDialog = ChooserDialog(activity!!, { initiateScan() }, { selectQrFromGallery() })
    }

    override fun inject() {
        (activity!!.application as IrohaSampleApp)
            .appComponent
            .mainComponentBuilder()
            .withActivity(activity as AppCompatActivity)
            .build()
            .inject(this)
    }

    override fun subscribe(viewModel: MainViewModel) {
        observe(viewModel.transferLiveData, Observer {
            to.setText(it.accountId.username())
            amount.setText(it.amount)
        })
    }

    private fun initiateScan() {
        RxPermissions(this)
            .request(Manifest.permission.CAMERA)
            .subscribe {
                if (it) integrator.initiateScan()
            }
    }

    private fun selectQrFromGallery() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }

        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_qr)), PICK_IMAGE_REQUEST)
    }

    private fun showQrChooser() {
        chooserDialog.show()
    }
}
