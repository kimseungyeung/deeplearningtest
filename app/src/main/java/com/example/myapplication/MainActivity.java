package com.example.myapplication;

import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.bytedeco.javacv.FrameFilter;
import org.deeplearning4j.models.word2vec.Word2Vec;
import org.deeplearning4j.text.sentenceiterator.BasicLineIterator;
import org.deeplearning4j.text.sentenceiterator.FileSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentenceIterator;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    String filePath;
    SentenceIterator iter;
    Button btn_learning;
    EditText edt_word;
    TextView tv_result;
    Word2Vec vec=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        component();

    }
    public void component(){
        tv_result= (TextView)findViewById(R.id.tv_result);
        edt_word =(EditText)findViewById(R.id.edt_word);
        btn_learning=(Button)findViewById(R.id.btn_learning);
        btn_learning.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_learning:

                    Uri url = Uri.parse("android.resource://"+ getPackageName() + "/" +"raw/" + "resulttext");
                    String path ="";
                    File localFile = new File(Environment.getExternalStorageDirectory(), "raw_sentences.txt");
                    iter = new FileSentenceIterator(localFile);

                TokenizerFactory t = new DefaultTokenizerFactory();
                t.setTokenPreProcessor(new CommonPreprocessor());
//        log.info("Building model....");
                Word2Vec vec = new Word2Vec.Builder()
                        .minWordFrequency(5)
                        .iterations(1)
                        .layerSize(100)
                        .seed(42)
                        .windowSize(5)
                        .iterate(iter)
                        .tokenizerFactory(t)
                        .batchSize(1000)
                        .build();


                vec.fit();
                String word =edt_word.getText().toString().trim();
                Collection<String> lst = vec.wordsNearestSum(word, 10);
                tv_result.append(lst.toString());
                break;
        }
    }





}
