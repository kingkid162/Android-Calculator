package xyz.hello_universe.scalculator;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    // Lưu lại biểu thức vào chuỗi
    private String screen = "";
    // EditText hiển thị biểu thức
    private EditText screenMath;
    // TextView hiển thị kết quả
    private TextView Ans;
    // Lưu lại kết quả
    private double ans;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        // Khởi tạo các EditText hiện biểu thức
        screenMath = (EditText) findViewById(R.id.editText);
        // Khởi tạo TextView hiện kết quả
        Ans = (TextView) findViewById(R.id.textView);

        // Chỉnh lại font cho ứng dụng
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/CaviarDreams.ttf");
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/ostrich-regular.ttf");
        FontsOverride.setDefaultFont(this, "SERIF", "fonts/CaviarDreams.ttf");
        FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/CaviarDreams.ttf");
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        return super.onCreateOptionsMenu(menu);
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.cal_menu, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        Intent intent = new Intent(MainActivity.this, About.class);
//        MainActivity.this.startActivity(intent);
//        return super.onOptionsItemSelected(item);
//    }

    /**
     * Xử lý sự kiện Onclick khi bấm 1 nút
     * @param view nút đang bấm
     */
    public void onClicked(View view) {
        Button btn = (Button) view;
        String text = btn.getText().toString();

        // Gọi phương thức và set lại background của nút khi ấn
        if (text.equals("AC")) {
            btn.setBackgroundResource(R.drawable.button2);
            reset();
        } else if (text.equals("DEL")) {
            btn.setBackgroundResource(R.drawable.button3);
            Delete();
        } else if (text.equals(".")){
            btn.setBackgroundResource(R.drawable.button3);
            Dot();
        } else if (text.equals("=")) {
            btn.setBackgroundResource(R.drawable.button);
            Equals();
        } else if (isNumber(text.charAt(0)) || text.charAt(0) == '(') { // Nếu là số nguyên và dấu mở ngoặc thì đưa vào screen
            btn.setBackgroundResource(R.drawable.button3);
            screen += text;
            screenMath.setText(screen);
            calculator();
        } else if (text.equals(")")) {
            btn.setBackgroundResource(R.drawable.button3);
            closeBracket();
        } else if (isOperator(text.charAt(0)) ){    // Xử lý khi ấn toán tử
            btn.setBackgroundResource(R.drawable.button2);
            // Thêm dấu trừ nếu là số âm
            if (screen.length()==0 && text.equals("-")){
                screen += "-";
            }
            // Nếu biểu thức không rỗng và kí tự cuối cùng không phải là toán tử thì thì thêm toán tử vào biểu thức
            if (!screen.equals("") && !isOperator(screen.charAt(screen.length()-1)) && screen.charAt(screen.length()-1)!='(') {
                screen += text;
            }
            screenMath.setText(screen);
            calculator();
        }
    }

    /**
     * Xử lý việc nhập dấu đóng ngoặc của biểu thức
     * Số dấu mở ngoặc không lớn hơn hoặc bằng số dấu đóng ngoặc
     */
    private void closeBracket() {
        if (screen.length()>0) {
            int open = 0;
            int close = 0;
            for (int i = 0; i < screen.length(); i++) {
                if (screen.charAt(i) == '(')
                    open++;                 // Đếm số dấu mở ngoặc trong biểu thức
                if (screen.charAt(i) == ')')
                    close++;                // Đếm số dấu đóng ngoặc trong biểu thức
            }
            // Nếu số dấu đóng ngoặc ít hơn dấu mở ngoặc và kí tự cuối cùng của biểu thức
            // là số thì thêm dấu đóng ngoặc vào biểu thức
            if (open>close && isNumber(screen.charAt(screen.length()-1)) ) {
                screen += ')';
                screenMath.setText(screen);
            }
        }
    }

    /**
     * Xử lý dấu phẩy
     */
    public void Dot() {
        if (screen.equals("")) {
            screen="0.";
            ans = 0;
        } else {
            int i = screen.length() - 1;
            while(isNumber(screen.charAt(i)) && i>0)
                i--;
            if (!screen.substring(i,screen.length()).contains(".")){
                screen += ".";
                screenMath.setText(screen);
                calculator();
            }
        }
    }

    /**
     * Khi ấn dấu bằng thì tính toán kết quả và đưa lên màn hình EditText
     * Bắt ngoại lệ với trường hợp chia cho 0
     */
    public void Equals() {
        if (screen.length()>0 && (isNumber(screen.charAt(screen.length()-1)) || screen.charAt(screen.length()-1) == ')')) {
            try {
                screen = ""+ Calculator(screen);
                ans = 0;
                screenMath.setText(screen);
                Ans.setText("");
            } catch (IllegalArgumentException e) {
                Ans.setText("ERROR!");
            }
        } else {
            Ans.setText("ERROR!");
        }
    }

    /**
     * Xoá từng kí tự từ cuối biểu thức và tính toán lại biểu thức hiển thị kết quả lên mà hình
     */
    public void Delete() {
        if (screen.length()>0){
            screen = screen.substring(0,screen.length()-1);
        }
        screenMath.setText(screen);
        calculator();
    }

    /**
     * Reset lại các biến nhớ và xoá màn hình
     */
    public void reset() {
        screen = "";
        ans = 0;
        screenMath.setText(screen);
        Ans.setText("");
    }

    /**
     * Tính toán và xử lý ngoại lệ , đưa kết quả hiển thị lên màn hình TextView
     */
    public void calculator() {
        if (screen.length()>0 && (isNumber(screen.charAt(screen.length()-1)) || screen.charAt(screen.length()-1) == ')')) {
            try {
                ans = Calculator(screen);
                Ans.setText(Double.toString(ans));
            } catch (IllegalArgumentException e) {
                Ans.setText("ERROR!");
            }
        }
    }

    /**
     * Xac dinh do uu tien cua cac toan tu
     *
     * @param c toan tu
     * @return do uu tien cua toan tu , cang cao cang uu tien
     */
    private int Priority(char c) {
        if (c == '+' || c == '-') {
            return 1;
        }
        if (c == '*' || c == '/') {
            return 2;
        }
        if (c == '~')
            return 3;
        return 0;
    }

    /**
     * Xác định 1 kí tự có phải số hạng
     * @param c kí tự trong biểu thức
     * @return true nếu c là số
     */
    private boolean isNumber(char c) {
        return (Character.isDigit(c)) || (c == '.');
    }

    /**
     * Xác định 1 kí tự có phải toán tử không
     * @param c
     * @return
     */
    private boolean isOperator(char c) {
        return (c == '+' || c == '-' || c == '*' || c == '/' || c =='~');
    }

    /**
     * Làm tròn số
     * @param num số cần được làm tròn
     * @param size  số kí tự của số
     * @return số đã được làm tròn đến size - 1 số thập phân
     */
    private Double myRound(double num, int size) {
        int n = size - Long.toString((long) num).length();
        Double number;
        number = Math.round(num * Math.pow(10, n)) / Math.pow(10, n);
        return number;
    }

    /**
     * Chuẩn hoá biểu thức
     * @param s chuỗi biểu thức cần được chuẩn hoá
     * @return chuỗi đã được chuẩn hoá
     */
    public String Standardize(String s){
        String s1 = "";
        s = s.trim();
        s = s.replaceAll("\\s+"," "); //	chuẩn hoá s
        int open = 0, close = 0;
        for (int i=0; i<s.length(); i++){
            char c = s.charAt(i);
            if (c == '(') open++;
            if (c == ')') close++;
        }
        for (int i=0; i< (open - close); i++) // thêm các dấu "(" vào cuối nếu thiếu
            s += ')';
        for (int i=0; i<s.length(); i++){
            //	chuyển ...)(... thành ...)*(...
            if (i>0 && s.charAt(i) == '(' && (s.charAt(i-1) == ')' || isNumber(s.charAt(i-1)))) s1 = s1 + "*";
            if ((i == 0 || (i>0 && !isNumber(s.charAt(i - 1)))) && s.charAt(i) == '-' && isNumber(s.charAt(i + 1))) {
                s1 = s1 + "~"; // check số âm
            }
            else s1 = s1 + s.charAt(i);
        }
        return s1;
    }

    /**
     * Chuyển biểu thức dạng Infix sang dạng PostFix
     * @param screen Biểu thức cần được chuyển sang dạng postFix
     * @return biểu thức dạng PostFix
     */
    private String PostFix(String screen) {
        String result = "";

        Stack<Character> operators = new Stack<Character>(); // Stack lưu các toán tử

        for (int i = 0; i < screen.length(); i++) {

            // Nếu là toán tử
            if (isOperator(screen.charAt(i))) {
                // Nếu stack không rỗng và toán tử trong stack có độ ưu tiên cao hơn tóan tử trong chuỗi đầu vào
                while (!operators.empty() && Priority(operators.peek()) >= Priority(screen.charAt(i))) {
                        result += operators.pop();
                }
                operators.push(screen.charAt(i));
            }

            // Nếu là số hạng
            if (isNumber(screen.charAt(i))) {

                // Tách số từ vị trí đang xét
                int pos = i;
                while (isNumber(screen.charAt(i))) {
                    i++;
                    if (i == screen.length())
                        break;
                }
                // Thêm dấu # để ngăn cách giữ các số hạng
                result += screen.substring(pos, i) + '#';
                i--;
            }
            // Nếu gặp dấu "(" thì cho vào stack
            if (screen.charAt(i) == '(') {
                operators.push('(');
            }
            // Nếu gặp dấu ")" thì đưa các toán tử trong stack vào chuỗi đầu ra cho đến khi gặp "("
            if (screen.charAt(i) == ')') {
                Character temp = operators.pop();
                while (temp != '(') {
                    result += temp;
                    temp = operators.pop();
                }
            }
        }
        // Đưa những toán tử còn lại vào chuỗi đầu ra
        while (!operators.isEmpty()) {
            result += operators.peek();
            operators.pop();
        }
        return result;
    }

    /**
     * Tính toán kết quả phép tính từ chuỗi đã được chuẩn hoá và chuyển sang dạng
     * Reverse Polish Notation
     * @param str là 1 chuỗi biểu thức dạng postfix
     * @return  kết quả phép tính double
     */
    public Double Calculator(String str) throws IllegalArgumentException{ // Ném ngoại lệ chia cho 0

        String input = Standardize(str); // Chuẩn hoá chuỗi
        String expression = PostFix(input); // Infix to PostFix

        Stack<Double> cal = new Stack<Double>();
        Double temp1, temp2;

        //Xử lý các phép toán với stack

        for (int i = 0; i < expression.length(); i++) {
            switch (expression.charAt(i)) {
                case '~' :
                    temp1 = cal.pop();
                    cal.push(-temp1);
                    break;
                case '*':
                    temp1 = cal.pop();
                    temp2 = cal.pop();
                    cal.push(temp1 * temp2);
                    break;
                case '/':
                    temp1 = cal.pop();
                    temp2 = cal.pop();
                   if (temp1 == 0) {
                        throw new IllegalArgumentException();
                    } else {
                        cal.push(temp2 / temp1);
                    }
                    break;
                case '+':
                    temp1 = cal.pop();
                    temp2 = cal.pop();
                    cal.push(temp1 + temp2);
                    break;
                case '-':
                    temp1 = cal.pop();
                    temp2 = cal.pop();
                    cal.push(temp2 - temp1);
                    break;
                default:
                    if (isNumber(expression.charAt(i))) {
                        int pos = i;
                        while (isNumber(expression.charAt(i)))
                            i++;
                        cal.push(Double.parseDouble(expression.substring(pos,i)));
                        i--;
                    }
                    break;
            }
        }
        return myRound(cal.pop(), 6);
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://xyz.hello_universe.scalculator/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Main Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://xyz.hello_universe.scalculator/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
