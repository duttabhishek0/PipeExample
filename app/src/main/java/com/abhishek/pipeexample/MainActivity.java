package com.abhishek.pipeexample;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.abhishek.pipeexample.databinding.ActivityMainBinding;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;

public class MainActivity  extends AppCompatActivity {
    private static final String TAG  = "PipeExampleActivity";
    private EditText editText;

    PipedReader r;
    PipedWriter w;

    private Thread workerThread;

    /**
     *
     * @param savedInstance
     */
    public void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        ActivityMainBinding binding = DataBindingUtil.setContentView(
                this,
                R.layout.activity_main
        );

        r = new PipedReader();
        w = new PipedWriter();


        try{
            r.connect(w);
        } catch (IOException e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main);

        binding.etHead.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                try{
                    // Handle addition of text
                    if(count > before){
                        // Write the last entered text to the pipe
                        w.write(charSequence.subSequence(before,count).toString());
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        workerThread = new Thread(new TextHandlerTask(r));
        workerThread.start();
    }

    private class TextHandlerTask implements Runnable {
        private final PipedReader reader;
        public TextHandlerTask(PipedReader reader) {
            this.reader = reader;
        }

        @Override
        public void run() {
            while(Thread.currentThread().isInterrupted()){
                try{
                    int i;
                    while((i = reader.read()) != -1){
                        char c = (char)i;

                        Log.d(TAG,
                                "char = " + c);
                    }
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}
