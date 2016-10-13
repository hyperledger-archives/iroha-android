package io.soramitsu.iroha;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.kobaken0029.ed25519.Ed25519;

import net.i2p.crypto.eddsa.KeyPairGenerator;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            SharedPreferences sp = getSharedPreferences("iroha", Context.MODE_PRIVATE);

            if (!sp.getBoolean("first", false)) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("first", true);

                KeyPairGenerator kpg = new KeyPairGenerator();
                KeyPair keyPair = kpg.generateKeyPair();

                byte[] privateKey = keyPair.getPrivate().getEncoded();
                byte[] publicKey = keyPair.getPublic().getEncoded();

                editor.putString("privateKey", new String(privateKey));
                editor.putString("publicKey", new String(publicKey));
                editor.apply();
            }

            byte[] privateKeyBytes = sp.getString("privateKey", "").getBytes();
            byte[] publicKeyBytes = sp.getString("publicKey", "").getBytes();


            Ed25519.KeyPair result = Ed25519.createKeyPair();
            if (result.getPublicKey() != null && result.getPrivateKey() != null) {
                Log.d("privateKey", new String(result.getPublicKey()));
                Log.d("publicKey", new String(result.getPrivateKey()));
            }

            String mimorin = "みもりん";
            byte[] signature = Ed25519.sign(result.getPublicKey(), result.getPrivateKey(), mimorin.getBytes());
            if (signature != null) {
                Log.d("signature", new String(signature));
                Log.d("verify", String.valueOf(Ed25519.verify(result.getPublicKey(), signature, mimorin.getBytes())));
            }

            TextView t = (TextView) findViewById(R.id.text);
            t.setText("");
            TextView t1 = (TextView) findViewById(R.id.text1);
            t1.setText("");
            TextView t2 = (TextView) findViewById(R.id.text2);
            t2.setText("");
//            TextView t3 = (TextView) findViewById(R.id.text3);
//            t3.setText(String.valueOf(new String(privateKeyBytes)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] convertObjToBytes(Object obj) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(obj);
        oos.close();
        baos.close();
        return baos.toByteArray();
    }
}
