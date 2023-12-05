package com.example.calculater2;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private TextView displytxv;
    private StringBuilder input ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        displytxv = findViewById(R.id.displayTextView);
        input = new StringBuilder();

        setButtonClickListeners();
    }

    private void setButtonClickListeners() {
        int[] numbersButtonIds = {
                R.id.button0,R.id.button1,R.id.button2,R.id.button3,R.id.button4,
                R.id.button5,R.id.button6,R.id.button7,R.id.button8,R.id.button9
        };
        int[] operatorButtonIds = {
                R.id.buttonplus,R.id.buttonminus,R.id.buttonmultiply,R.id.buttondevide
        };

        // number buttons
        for (int id : numbersButtonIds){
            findViewById(id).setOnClickListener(view -> appendToInput(((Button)view).getText().toString()));
        }

        // operator buttons
        for (int id : operatorButtonIds){
            findViewById(id).setOnClickListener(view -> appendToInput(((Button)view).getText().toString()));
        }

        findViewById(R.id.buttonclear).setOnClickListener(view -> clearInput());
        findViewById(R.id.buttondelete).setOnClickListener(view -> deleteLastInput());
        findViewById(R.id.buttonequal).setOnClickListener(view -> evaluateExpresion());
    }

    private void evaluateExpresion() {
        try{
            String result = String.valueOf(eval(input.toString()));
            input.setLength(0);
            input.append(result);
            updateDisplay();

        }catch (Exception e){
            input.setLength(0);
            input.append("Error");
            updateDisplay();
        }
    }

    private double eval(final String str) {
        return new Object(){
            int pos = -1 ;
            int ch ;
            void nextChar(){
                ch = (++pos < str.length())? str.charAt(pos):-1;

            }
            boolean isDigit(){
                return Character.isDigit(ch);
            }
            double parse(){
                nextChar();
                double x =parseExpression();
                if (pos < str.length()) {
                    throw new RuntimeException("unexpected: "+(char)ch);
                }
                return x;
            }
            double parseExpression(){
                double x = parseTerm();
                for (; ;) {
                    if (eat('+'))  {
                        x+=parseTerm();
                    } else if (eat('-')) {
                        x-=parseTerm();
                    }else {
                        return x;
                    }
                }
            }
            double parseTerm(){
                double x = parseFactor();

                for (; ; ) {
                    if (eat('*'))  {
                        x*=parseFactor();
                    } else if (eat('/')) {
                        x/=parseFactor();
                    }else {
                        return x;
                    }
                }
            }
            double parseFactor(){
                if (eat('+'))  {
                    return parseFactor();
                }
                if (eat('-')) {
                    return -parseFactor();  }
                double x;
                int startPos = this.pos;

                if(eat('(')){
                    x= parseExpression();
                    eat(')');
                } else if (isDigit() || ch == '.') {
                    while (isDigit()|| ch=='.') nextChar();
                    x = Double.parseDouble(str.substring(startPos,this.pos));
                }else {
                    throw new RuntimeException("Unexpected: "+(char)ch);
                }
                return x;
            }
            boolean eat(int chartoeat){
                while (ch == ' ') nextChar();

                if(ch == chartoeat){
                    nextChar();
                    return true;
                }
                return false;
            }
        }.parse();

    }

    private void deleteLastInput() {
        if(input.length()>0){
            input.deleteCharAt(input.length()-1);
            updateDisplay();
        }
    }

    private void clearInput() {
        input.setLength(0);
        updateDisplay();
    }

    private void appendToInput(String value) {
        input.append(value);
        updateDisplay();
    }

    private void updateDisplay() {
        displytxv.setText(input.toString());
    }
}