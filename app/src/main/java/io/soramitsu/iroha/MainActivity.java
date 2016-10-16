package io.soramitsu.iroha;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.kobaken0029.ed25519.Ed25519;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            SharedPreferences sp = getSharedPreferences("iroha-android", Context.MODE_PRIVATE);

            Ed25519.KeyPair keyPair;
            if (!sp.getBoolean("first", false)) {
                Log.d("test", "First!!!!!");
                 keyPair = Ed25519.createKeyPair();

                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("first", true);
                editor.putString("publicKey", keyPair.getPublicKey());
                editor.putString("privateKey", keyPair.getPrivateKey());
                editor.apply();
            } else {
                Log.d("test", "Created KeyPair!!!!!");
                keyPair = new Ed25519.KeyPair(
                        sp.getString("publicKey", ""),
                        sp.getString("privateKey", "")
                );
            }

            String message = "Hello IrohaAndroid!";
            String signature = Ed25519.sign(message, keyPair);
            boolean verify = Ed25519.verify(signature, message, keyPair.getPrivateKey());

            TextView t = (TextView) findViewById(R.id.text);
            t.setText(keyPair.getPublicKey());
            TextView t1 = (TextView) findViewById(R.id.text1);
            t1.setText(keyPair.getPrivateKey());
            TextView t2 = (TextView) findViewById(R.id.text2);
            t2.setText(signature);
            TextView t3 = (TextView) findViewById(R.id.text3);
            t3.setText(String.valueOf(verify));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
