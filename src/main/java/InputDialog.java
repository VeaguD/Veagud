

import javax.swing.*;
import java.awt.*;

public class InputDialog extends JDialog {
    boolean selectedValue;
    int otstupValue;
    int shirinaPloshadki;
    private JTextField width;
    private JTextField length;
    private JTextField height;
    private JTextField chistovoiPol;
    private JTextField shirinamarsha;
    private JTextField otstupAuto;
    private JTextField otstupMono;
    private JTextField ploshadkaShirinaTeor;
    private JTextField ploshadkaShirina;
    private JTextField minVValue;
    private JTextField maxVValue;
    private JTextField minT;
    private JTextField maxT;
    private JTextField lengthOtStenDoCraya;
    private boolean isSubmitted = false;
    private JCheckBox hasNogiAuto;
    private JCheckBox hasNogiMono;
    private boolean isSelectedTab = false;

    private JTextField ploshadka;
    private JTextField stupenGlubina;
    //    private JTextField otstup;
    private JTextField lowerStairsCount;
    private JTextField upperStairsCount;
    private JTextField heightStupen;

    private JTextField betweenMarsh;

    public InputDialog(Frame parent) {


        super(parent, "Введите данные", true);
        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel firstTabPanel = new JPanel();
        firstTabPanel.setLayout(new BoxLayout(firstTabPanel, BoxLayout.Y_AXIS));

        setLayout(new GridLayout(11, 10));

        JLabel label1 = new JLabel("Введите ширину проёма:");
        JLabel label2 = new JLabel("Введите длинну проёма:");
        JLabel label3 = new JLabel("Введите чистовую высоту:");
        JLabel label4 = new JLabel("Чистовой пол второго этажа:");
        JLabel label5 = new JLabel("Ширина марша:");
        JLabel label5P2 = new JLabel("Ширина марша:");
        JLabel label6 = new JLabel("Отступ:");
        JLabel label6P2 = new JLabel("Отступ:");
        JLabel label7 = new JLabel("Площадка теоретическая:");
        JLabel label8 = new JLabel("Диапазон высоты ступени от:");
        JLabel label9 = new JLabel("Диапазон высоты ступени до:");
        JLabel label10 = new JLabel("Диапазон глубины ступени от:");
        JLabel label11 = new JLabel("Диапазон глубины ступени до:");
        JLabel label12 = new JLabel("Ограничение расстояния на нижнем этаже:");
        JLabel label13 = new JLabel("Глубина площадки:");
        JLabel label14 = new JLabel("Глубина ступени:");
        JLabel label15 = new JLabel("Количество ступеней в нижнем марше:");
        JLabel label16 = new JLabel("Количество ступеней в верхнем марше:");
        JLabel label17 = new JLabel("Высота ступени:");
        JLabel label18 = new JLabel("Расстояние между маршей:");


        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS)); // Вертикальное выравнивание

        // Создаём панель и добавляем к ней метку и поле ввода
        JPanel widthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        width = new JTextField("2000", 10);  // Задайте ширину поля ввода
        widthPanel.add(label1);
        widthPanel.add(width);
        add(widthPanel);
        firstTabPanel.add(widthPanel);

        JPanel lengthPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        length = new JTextField("3500", 10);  // Задайте ширину поля ввода
        lengthPanel.add(label2);
        lengthPanel.add(length);
        add(lengthPanel);
        firstTabPanel.add(lengthPanel);

        JPanel heightPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        height = new JTextField("3300", 10);  // Задайте ширину поля ввода
        heightPanel.add(label3);
        heightPanel.add(height);
        add(heightPanel);
        firstTabPanel.add(heightPanel);

        JPanel chistovoiPolPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        chistovoiPol = new JTextField("20", 10);  // Задайте ширину поля ввода
        chistovoiPolPanel.add(label4);
        chistovoiPolPanel.add(chistovoiPol);
        add(chistovoiPolPanel);
        firstTabPanel.add(chistovoiPolPanel);

        JPanel shirinamarshaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        shirinamarsha = new JTextField("900", 10);  // Задайте ширину поля ввода
        shirinamarshaPanel.add(label5);
        shirinamarshaPanel.add(shirinamarsha);
        add(shirinamarshaPanel);
        firstTabPanel.add(shirinamarshaPanel);


        JPanel otstupPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        otstupAuto = new JTextField("20", 10);  // Задайте ширину поля ввода
        otstupPanel.add(label6);
        otstupPanel.add(otstupAuto);
        add(otstupPanel);
        firstTabPanel.add(otstupPanel);

        JPanel ploshadkaShirinaTeorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ploshadkaShirinaTeor = new JTextField("900", 10);  // Задайте ширину поля ввода
        ploshadkaShirinaTeorPanel.add(label7);
        ploshadkaShirinaTeorPanel.add(ploshadkaShirinaTeor);
        add(ploshadkaShirinaTeorPanel);
        firstTabPanel.add(ploshadkaShirinaTeorPanel);

        JPanel minVValuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        minVValue = new JTextField("120", 10);  // Задайте ширину поля ввода
        minVValuePanel.add(label8);
        minVValuePanel.add(minVValue);
        add(minVValuePanel);
        firstTabPanel.add(minVValuePanel);

        JPanel maxVValuePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        maxVValue = new JTextField("200", 10);  // Задайте ширину поля ввода
        maxVValuePanel.add(label9);
        maxVValuePanel.add(maxVValue);
        add(maxVValuePanel);
        firstTabPanel.add(maxVValuePanel);

        JPanel minTPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        minT = new JTextField("250", 10);  // Задайте ширину поля ввода
        minTPanel.add(label10);
        minTPanel.add(minT);
        add(minTPanel);
        firstTabPanel.add(minTPanel);

        JPanel maxTPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        maxT = new JTextField("310", 10);  // Задайте ширину поля ввода
        maxTPanel.add(label11);
        maxTPanel.add(maxT);
        add(maxTPanel);
        firstTabPanel.add(maxTPanel);

        JPanel lengthOtStenDoCrayaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lengthOtStenDoCraya = new JTextField("3500", 10);  // Задайте ширину поля ввода
        lengthOtStenDoCrayaPanel.add(label12);
        lengthOtStenDoCrayaPanel.add(lengthOtStenDoCraya);
        add(lengthOtStenDoCrayaPanel);
        firstTabPanel.add(lengthOtStenDoCrayaPanel);

        JPanel hasNogiPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        hasNogiAuto = new JCheckBox("Нарисовать опорные ноги ?");
//        hasNogiAuto.setSelected(true);
        hasNogiPanel.add(hasNogiAuto);
        add(hasNogiPanel);
        firstTabPanel.add(hasNogiPanel);

        tabbedPane.addTab("Вкладка для ввода данных", firstTabPanel);


        JPanel secondTabPanel = new JPanel();
        secondTabPanel.setLayout(new BoxLayout(secondTabPanel, BoxLayout.Y_AXIS));

        JPanel ploshadkaPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        ploshadkaShirina = new JTextField("900", 10);  // Задайте ширину поля ввода
        ploshadkaPanel2.add(label13);
        ploshadkaPanel2.add(ploshadkaShirina);
        ploshadkaPanel2.setMaximumSize(new Dimension(Integer.MAX_VALUE, ploshadkaPanel2.getPreferredSize().height));
        add(ploshadkaPanel2);
        secondTabPanel.add(ploshadkaPanel2);

        JPanel stupenGlubinaPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        stupenGlubina = new JTextField("250", 10);  // Задайте ширину поля ввода
        stupenGlubinaPanel.add(label14);
        stupenGlubinaPanel.add(stupenGlubina);
        stupenGlubinaPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, stupenGlubinaPanel.getPreferredSize().height));
        add(stupenGlubinaPanel);
        secondTabPanel.add(stupenGlubinaPanel);

        JPanel lowerStairsCountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        lowerStairsCount = new JTextField("10", 10);  // Задайте ширину поля ввода
        lowerStairsCountPanel.add(label15);
        lowerStairsCountPanel.add(lowerStairsCount);
        lowerStairsCountPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, lowerStairsCountPanel.getPreferredSize().height));
        add(lowerStairsCountPanel);
        secondTabPanel.add(lowerStairsCountPanel);

        JPanel upperStairsCountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        upperStairsCount = new JTextField("10", 10);  // Задайте ширину поля ввода
        upperStairsCountPanel.add(label16);
        upperStairsCountPanel.add(upperStairsCount);
        upperStairsCountPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, upperStairsCountPanel.getPreferredSize().height));
        add(upperStairsCountPanel);
        secondTabPanel.add(upperStairsCountPanel);

        JPanel heightStupenPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        heightStupen = new JTextField("150", 10);  // Задайте ширину поля ввода
        heightStupenPanel.add(label17);
        heightStupenPanel.add(heightStupen);
        heightStupenPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, heightStupenPanel.getPreferredSize().height));
        add(heightStupenPanel);
        secondTabPanel.add(heightStupenPanel);

        JPanel otstupPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        otstupMono = new JTextField("20", 10);  // Задайте ширину поля ввода
        otstupPanel2.add(label6P2);
        otstupPanel2.add(otstupMono);
        otstupPanel2.setMaximumSize(new Dimension(Integer.MAX_VALUE, otstupPanel2.getPreferredSize().height));
        add(otstupPanel2);
        secondTabPanel.add(otstupPanel2);

        JPanel shirinamarshaPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        shirinamarsha = new JTextField("900", 10);  // Задайте ширину поля ввода
        shirinamarshaPanel2.add(label5P2);
        shirinamarshaPanel2.add(shirinamarsha);
        shirinamarshaPanel2.setMaximumSize(new Dimension(Integer.MAX_VALUE, shirinamarshaPanel2.getPreferredSize().height));
        add(shirinamarshaPanel2);
        secondTabPanel.add(shirinamarshaPanel2);

        JPanel betweenMarshPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        betweenMarsh = new JTextField("100", 10);  // Задайте ширину поля ввода
        betweenMarshPanel.add(label18);
        betweenMarshPanel.add(betweenMarsh);
        betweenMarshPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, betweenMarshPanel.getPreferredSize().height));
        add(betweenMarshPanel);
        firstTabPanel.add(betweenMarshPanel);
        secondTabPanel.add(betweenMarshPanel);

        JPanel hasNogiPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        hasNogiMono = new JCheckBox("Нарисовать опорные ноги ?");
//        hasNogiMono.setSelected(true);
        hasNogiPanel2.add(hasNogiMono);
        hasNogiPanel2.setMaximumSize(new Dimension(Integer.MAX_VALUE, hasNogiPanel2.getPreferredSize().height));
        add(hasNogiPanel2);
        secondTabPanel.add(hasNogiPanel2);

        tabbedPane.addTab("Вкладка для ввода данных", secondTabPanel);
        tabbedPane.addChangeListener(e -> {
            isSelectedTab = (tabbedPane.getSelectedIndex() == 1);
        });

        add(tabbedPane);


        JPanel submitPanel = new JPanel();
        JButton submitButton = new JButton("Подтвердить");
        submitButton.addActionListener(e -> {
            isSubmitted = true;
            if (isSelectedTab) {
                 selectedValue = hasNogiMono.isSelected();
                 otstupValue = Integer.parseInt(otstupMono.getText());
                 shirinaPloshadki = Integer.parseInt(ploshadkaShirina.getText());
            } else {
                 selectedValue = hasNogiAuto.isSelected();
                 otstupValue = Integer.parseInt(otstupAuto.getText());
                shirinaPloshadki = Integer.parseInt(ploshadkaShirinaTeor.getText());
            }
            dispose();
        });
        submitPanel.add(submitButton);
        add(submitPanel);


        pack();

        setLocationRelativeTo(parent);
        setVisible(true);
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }

    public String getWidthText() {
        return width.getText();
    }

    public String getLengthText() {
        return length.getText();
    }

    public String getHeightText() {
        return height.getText();
    }

    public String getChistovoiPolText() {
        return chistovoiPol.getText();
    }

    public String getShirinamarshaText() {
        return shirinamarsha.getText();
    }

    public String getOtstupText() {
        return otstupAuto.getText();
    }

    public String getPloshadkaShirinaTeorText() {
        return ploshadkaShirinaTeor.getText();
    }

    public String getMinVValueText() {
        return minVValue.getText();
    }

    public String getMaxVValueText() {
        return maxVValue.getText();
    }

    public String getMinTText() {
        return minT.getText();
    }

    public String getMaxTText() {
        return maxT.getText();
    }

    public String getLengthOtStenDoCrayaText() {
        return lengthOtStenDoCraya.getText();
    }

    public boolean getHasNogiValue() {
        return hasNogiAuto.isSelected();
    }

    public String getPloshadkaText() {
        return ploshadka.getText();
    }

    public String getStupenGlubinaText() {
        return stupenGlubina.getText();
    }

    public String getLowerStairsCountText() {
        return lowerStairsCount.getText();
    }

    public String getUpperStairsCountText() {
        return upperStairsCount.getText();
    }

    public String getHeightStupenText() {
        return heightStupen.getText();
    }

    public boolean isSelectedTab() {
        return isSelectedTab;
    }

    public void setSelectedTab(boolean selectedTab) {
        isSelectedTab = selectedTab;
    }

    public String getBetweenMarshText() {
        return betweenMarsh.getText();
    }

    public String getOtstupMono() {
        return otstupMono.getText();
    }

    public boolean getHasNogiMono() {
        return hasNogiAuto.isSelected();
    }

    public boolean isSelectedValue() {
        return selectedValue;
    }

    public String getOtstupValue() {
        return otstupValue + "";
    }

    public String getPloshadka() {
        return ploshadka.getText();
    }

    public String getPloshadkaShirina() {
        return ploshadkaShirina.getText();
    }

    public String getShirinaPloshadki() {
        return shirinaPloshadki+ "";
    }
}
