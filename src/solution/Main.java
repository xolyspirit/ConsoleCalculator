package solution;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Main {

    private String operators = "+-*/^";
    private String delimiters = "() " + operators;

    public static void main(String[] args) throws IOException {
        Main main = new Main();
        String expression = main.readExpression();
        ArrayList<String> polsk = main.toReversePolsk(expression);
        System.out.println(main.calculate(polsk));
    }
    //calculate
    private String calculate(ArrayList<String> expression){
        String answer;
        Deque<String> queue = new ArrayDeque<>();
        BigDecimal a,b,result = null;
        try{
            for (int i = 0; i < expression.size(); i++) {
                if (!isOperator(expression.get(i))){
                    queue.push(expression.get(i));
                }
                else {
                    b = new BigDecimal(queue.pop());
                    a = new BigDecimal(queue.pop());
                    switch (expression.get(i)){
                        case "+": result = a.add(b); break;
                        case "-": result = a.subtract(b); break;
                        //BigDecimal can not pow by fractional number, so use double for this
                        case "^":
                            Double aa = Double.parseDouble(a.toString());
                            Double bb = Double.parseDouble(b.toString());
                            result = new BigDecimal(Math.pow(aa,bb)); break;
                        case "/": result = a.divide(b,2, RoundingMode.HALF_UP); break;
                        case "*": result = a.multiply(b); break;
                    }
                    queue.add(result.toString());
                }
            }
            answer = result.toString();
        }
        catch (ArithmeticException e){
            answer = "Can not be divided into zero";
        }
        return answer;
    }
    //reading original expression
    private String readExpression() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Please input expression");
        String s = null;
        while (s==null){
            s = normalize(reader.readLine());
        }
        return s;
    }
    //check for mistakes during typing
    private String normalize(String str){
        str = str.replace(',','.');
        char[] arex = str.toCharArray();
        for (int i = 1; i <arex.length; i++) {
            if (isDigit(arex[i])||(delimiters.contains(arex[i]+ ""))){
                if (arex[i]==' '&&isDigit(arex[i-1])&&isDigit(arex[i+1])){
                    System.out.println("Expression is not valid");
                    return null;
                }
                if (arex[i]=='.'&&(arex[i-1]=='.'||arex[i+1]=='.')){
                    System.out.println("Expression is not valid");
                    return null;
                }
            }
            else {
                System.out.println("Expression is not valid");
                return null;
            }
        }

        String s = String.valueOf(arex);
        return s.replaceAll(" ","");
    }
    //use to turn into reverse Polish notation
    private ArrayList<String> toReversePolsk(String expression){

        HashMap<String, Integer> operations = new HashMap<>();
        operations.put("(",0);
        operations.put(")",0);
        operations.put("^",3);
        operations.put("*",2);
        operations.put("/",2);
        operations.put("+",1);
        operations.put("-",1);

        ArrayList<String> strings = new ArrayList<>();
        ArrayList<String> polskStrings = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();
        StringTokenizer tokenizer = new StringTokenizer(expression,delimiters,true);
        String temp = null, prev;
        while (tokenizer.hasMoreTokens()){
            prev = temp;
            temp = tokenizer.nextToken();
            if(temp.equals("-")&&(prev==null||isDelimiter(prev))){
                strings.add(temp + tokenizer.nextToken());
                continue;
            }
            strings.add(temp);
        }

        for (String s: strings) {
            if (isDelimiter(s)){
                if (s.equals("(")){
                    stack.push(s);
                }
                else if (s.equals(")")){
                    while (!stack.peek().equals("(")){
                        polskStrings.add(stack.pop());
                    }
                    stack.pop();
                }
                else{
                    while (stack.size()!=0&&operations.get(s)<=operations.get(stack.peek())){
                        polskStrings.add(stack.pop());
                    }
                    stack.push(s);
                }

            }
            else {
                polskStrings.add(s);
            }
        }
        while (stack.size()!=0){
            polskStrings.add(stack.pop());
        }
        return polskStrings;
    }
    //check for digit
    private boolean isDigit(char ch){
        if(ch>47&&ch<58){
            return  true;
        }
        else {
            return false;
        }
    }
    //check for delimiter
    private boolean isDelimiter(String token) {
        if (token.length() != 1) return false;
        for (int i = 0; i < delimiters.length(); i++) {
            if (token.charAt(0) == delimiters.charAt(i)) return true;
        }
        return false;
    }
    //check for operator
    private boolean isOperator(String token) {
        int j = token.length()-1;
        for (int i = 0; i < operators.length(); i++) {
            if (token.charAt(j) == operators.charAt(i)) return true;
        }
        return false;
    }
}

