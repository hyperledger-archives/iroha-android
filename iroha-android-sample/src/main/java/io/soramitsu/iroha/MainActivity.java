/*
Copyright Soramitsu Co., Ltd. 2016 All Rights Reserved.
http://soramitsu.co.jp

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

         http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package io.soramitsu.iroha;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import io.soramitsu.irohaandroid.KeyPair;

import static io.soramitsu.irohaandroid.Iroha.createKeyPair;
import static io.soramitsu.irohaandroid.Iroha.sha3_256;
import static io.soramitsu.irohaandroid.Iroha.sign;
import static io.soramitsu.irohaandroid.Iroha.verify;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            SharedPreferences sp = getSharedPreferences("iroha-android", Context.MODE_PRIVATE);

            KeyPair keyPair;
            if (!sp.getBoolean("first", false)) {
                Log.d("test", "First!!!!!");
                 keyPair = createKeyPair();

                SharedPreferences.Editor editor = sp.edit();
                editor.putBoolean("first", true);
                editor.putString("publicKey", keyPair.getPublicKey());
                editor.putString("privateKey", keyPair.getPrivateKey());
                editor.apply();
            } else {
                Log.d("test", "Created KeyPair!!!!!");
                keyPair = new KeyPair(
                        sp.getString("publicKey", ""),
                        sp.getString("privateKey", "")
                );
            }

            String message = sha3_256("Hello IrohaAndroid!");
            String signature = sign(keyPair, message);
            boolean verify = verify(keyPair.getPublicKey(), signature, message);

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
