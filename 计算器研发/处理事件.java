  /**
     * 处理事件
     */
    public void actionPerformed(ActionEvent e) {
        // 获取事件源的标签
        String label = e.getActionCommand();
        if (label.equals(COMMAND[0])) {
            // 用户按了"Backspace"键
            handleBackspace();
        } else if (label.equals(COMMAND[1])) {
            // 用户按了"CE"键
            resultText.setText("0");
        } else if (label.equals(COMMAND[2])) {
            // 用户按了"C"键
            handleC();
        } else if ("0123456789.".indexOf(label) >= 0) {
            // 用户按了数字键或者小数点键
            handleNumber(label);
            // handlezero(zero);
        } else {
            // 用户按了运算符键
            handleOperator(label);
        }
    }
 
    /**
     * 处理Backspace键被按下的事件
     */
    private void handleBackspace() {
        String text = resultText.getText();
        int i = text.length();
        if (i > 0) {
            // 退格，将文本最后一个字符去掉
            text = text.substring(0, i - 1);
            if (text.length() == 0) {
                // 如果文本没有了内容，则初始化计算器的各种值
                resultText.setText("0");
                firstDigit = true;
                operator = "=";
            } else {
                // 显示新的文本
                resultText.setText(text);
            }
        }
    }