package com.running.myapplication.Tokenizer;

import android.util.Log;

import com.twitter.penguin.korean.KoreanTokenJava;
import com.twitter.penguin.korean.TwitterKoreanProcessorJava;
import org.apache.commons.lang.StringUtils;
import org.deeplearning4j.text.tokenization.tokenizer.TokenPreProcess;
import org.deeplearning4j.text.tokenization.tokenizer.Tokenizer;
import scala.collection.Seq;

import java.util.*;

/**
 * Created by kepricon on 16. 10. 20. KoreanTokenizer using KoreanTwitterText
 * (<a href=
 * "https://github.com/twitter/twitter-korean-text">https://github.com/twitter/twitter-korean-text</a>)
 */

public class ExcelTokenizer implements Tokenizer {
    private Iterator<String> tokenIter;
    private List<String> tokenList;

    private TokenPreProcess preProcess;
    List<String> stopwords= new ArrayList<String>(Arrays.asList(" ", ".", ",", "?", "\"", "?", ">",
            "<", "\\", "%",
            "이니","이다","으로","다고","에서","이며","니다","하다","가서","하고","하실","라며","하냐","나면","하기","지는","등등","◆","하겠","됨"
            ,"서는","내려","하여","어져","~","하였","다했","지만","하면","로는","니까","입니","립니","하면","되냐","되는","°","는","를","해서"
            ,"하시","하세","하셔","하신","이나","않냐","어졌","하며","었음","하셨","했","됩","었","하였","해야","▲","★","☆","※","ㄱ","ㄴ","ㄷ","ㅁ"
            ,"\\/",":","^","(",")","-","[","]","/","\\","#","!"+"'","*","+","0","1","2","3","4","5","6","7","8","9","^","ㆍ"));
    boolean check=false;
    public ExcelTokenizer(String toTokenize, int type) {
        //    log.debug("{}", toTokenize);  토큰생성로그 텍스트가 많으면 로그가 뒤로 밀려서 진행상황 볼수가없음
        // need normalize?

        // Tokenize
        String[] values = toTokenize.split(",");

        if (values != null && values.length > 4 && StringUtils.isNotEmpty(values[3])) {

            Seq<com.twitter.penguin.korean.tokenizer.KoreanTokenizer.KoreanToken> tokens = TwitterKoreanProcessorJava
                    .tokenize(values[3]);
            tokenList = new ArrayList<>();
            Iterator<KoreanTokenJava> iter = TwitterKoreanProcessorJava.tokensToJavaKoreanTokenList(tokens).iterator();
            //문장단위로 끊어서 처리해야할경우
            if (type == 0) {
                while (iter.hasNext()) {
                    String addd = iter.next().getText().trim();
                    if (!addd.equals(",") && !addd.equals("") && !addd.equals("\"") &&
                            !addd.equals("[") && !addd.equals("]")&&!addd.trim().equals("기지")
                    ) {

                        for(int i=0; i<stopwords.size(); i++){
                            check=false;
                           check=addd.contains(stopwords.get(i));
                           if(check){
                               break;
                           }
                        }
                        if(!check) {
                            tokenList.add(addd);
                        }
                    }
                }
            }
            //단어로 표시해야할경우 (띄어쓰기 그대로 표시하기위함)
            else if (type == 1) {
                tokenList.add(values[3]);
            }
        }
        else {
            tokenList = new ArrayList<>();
        }

        tokenIter = tokenList.iterator();
    }

    @Override
    public boolean hasMoreTokens() {
        return tokenIter.hasNext();
    }

    @Override
    public int countTokens() {
        return tokenList.size();
    }

    @Override
    public String nextToken() {
        if (hasMoreTokens() == false) {
            throw new NoSuchElementException();
        }
        return this.preProcess != null ? this.preProcess.preProcess(tokenIter.next()) : tokenIter.next();
    }

    @Override
    public List<String> getTokens() {
        return tokenList;
    }

    @Override
    public void setTokenPreProcessor(TokenPreProcess tokenPreProcess) {
        this.preProcess = tokenPreProcess;
    }
}