

import javax.swing.*;
import java.awt.*;

public class InputDialog extends JDialog {
    boolean selectedValue;
    int otstupValue;
    int shirinaPloshadki;
    private JTextField width, length, height, chistovoiPol, shirinamarsha, otstupAuto, otstupMono,
            ploshadkaShirinaTeor, ploshadkaShirina, minVValue, maxVValue, minT, maxT, lengthOtStenDoCraya,
            ploshadka, stupenGlubina, lowerStairsCount, upperStairsCount, heightStupen, betweenMarsh, pathSaveMono,
            pathSaveAuto;
    private boolean isSubmitted = false;
    private JCheckBox hasNogiAuto, hasNogiMono;
    private boolean isSelectedTab = false;
    private JComboBox<String> directionSelectorSecondTab, directionSelectorFirstTab;
    private JComboBox<Integer> selectorFirstTabZabStupen;

    public InputDialog(Frame parent) {
        super(parent, "Давай нарисуем лестницу !", true);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Вкладка для ввода данных", createFirstTab());
        tabbedPane.addTab("Вкладка для ввода данных", createSecondTab());
        tabbedPane.addChangeListener(e -> isSelectedTab = (tabbedPane.getSelectedIndex() == 1));

        mainPanel.add(tabbedPane, BorderLayout.CENTER);
        mainPanel.add(createSubmitPanel(), BorderLayout.SOUTH);

        add(mainPanel);

        pack();
        setLocationRelativeTo(parent);
        setVisible(true);
    }


    private JPanel createFirstTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(createDirectionSelectorFirst());
        panel.add(createPanel("Введите ширину проёма:", width = new JTextField("2000", 10)));
        panel.add(createPanel("Введите длинну проёма:", length = new JTextField("3500", 10)));
        panel.add(createPanel("Введите чистовую высоту:", height = new JTextField("3300", 10)));
        panel.add(createPanel("Чистовой пол второго этажа:", chistovoiPol = new JTextField("20", 10)));
        panel.add(createPanel("Ширина марша:", shirinamarsha = new JTextField("900", 10)));
        panel.add(createPanel("Отступ:", otstupAuto = new JTextField("20", 10)));
        panel.add(createPanel("Площадка теоретическая:", ploshadkaShirinaTeor = new JTextField("900", 10)));
        panel.add(createPanel("Диапазон высоты ступени от:", minVValue = new JTextField("120", 10)));
        panel.add(createPanel("Диапазон высоты ступени до:", maxVValue = new JTextField("200", 10)));
        panel.add(createPanel("Диапазон глубины ступени от:", minT = new JTextField("250", 10)));
        panel.add(createPanel("Диапазон глубины ступени до:", maxT = new JTextField("310", 10)));
        panel.add(createPanel("Ограничение расстояния на нижнем этаже:", lengthOtStenDoCraya = new JTextField("3500", 10)));
        panel.add(createPanel("Введите путь для сохранения", pathSaveAuto =
                new JTextField("D:\\Final projects stair\\", 25)));
        panel.add(createCheckboxPanel("Нарисовать опорные ноги ?", hasNogiAuto = new JCheckBox()));
        return panel;
    }

    private JPanel createSecondTab() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.add(createDirectionSelectorSecond());
        panel.add(createZabStupenSelectorPanel());
        panel.add(createPanel("Глубина площадки:", ploshadkaShirina = new JTextField("900", 10)));
        panel.add(createPanel("Глубина ступени:", stupenGlubina = new JTextField("250", 10)));
        panel.add(createPanel("Количество ступеней в нижнем марше:", lowerStairsCount = new JTextField("10", 10)));
        panel.add(createPanel("Количество ступеней в верхнем марше:", upperStairsCount = new JTextField("10", 10)));
        panel.add(createPanel("Высота ступени:", heightStupen = new JTextField("150", 10)));
        panel.add(createPanel("Отступ:", otstupMono = new JTextField("20", 10)));
        panel.add(createPanel("Расстояние между маршей:", betweenMarsh = new JTextField("100", 10)));
        panel.add(createPanel("Введите путь для сохранения", pathSaveMono =
                new JTextField("D:\\Final projects stair\\", 25)));
        panel.add(createCheckboxPanel("Нарисовать опорные ноги ?", hasNogiMono = new JCheckBox()));
        return panel;
    }

    private JComboBox<String> createDirectionSelectorFirst() {
        String[] directions = {"Левый подъём", "Правый подъем"};
        directionSelectorFirstTab = new JComboBox<>(directions);
        return directionSelectorFirstTab;
    }

    private JComboBox<String> createDirectionSelectorSecond() {
        String[] directions = {"Левый подъём", "Правый подъем"};
        directionSelectorSecondTab = new JComboBox<>(directions);
        return directionSelectorSecondTab;
    }

    private JPanel createZabStupenSelectorPanel() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("Рисовать забежные ступени");

        JCheckBox checkBox = new JCheckBox();
        checkBox.addActionListener(e -> {
            if (checkBox.isSelected()) {
                selectorFirstTabZabStupen.setEnabled(true);
            } else {
                selectorFirstTabZabStupen.setEnabled(false);
                selectorFirstTabZabStupen.setSelectedItem(0); // устанавливаем значение на 0
            }
        });

        selectorFirstTabZabStupen = createSelectorCountZabStup();
        selectorFirstTabZabStupen.setEnabled(false); // по умолчанию деактивирован

        panel.add(label);
        panel.add(checkBox);
        panel.add(selectorFirstTabZabStupen);

        return panel;
    }

    private JComboBox<Integer> createSelectorCountZabStup() {
        Integer[] directions = {0, 2, 4, 6, 8};
        JComboBox<Integer> comboBox = new JComboBox<>(directions);
        return comboBox;
    }

    private JPanel createPanel(String labelText, JTextField textField) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(labelText));
        panel.add(textField);
        return panel;
    }

    private JPanel createCheckboxPanel(String labelText, JCheckBox checkBox) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.add(new JLabel(labelText));
        panel.add(checkBox);
        return panel;
    }

    private JPanel createSubmitPanel() {
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

        // Добавляем кнопку "Отмена"
        JButton cancelButton = new JButton("Галя, отмена!");
        cancelButton.addActionListener(e -> {
            isSubmitted = false; // Устанавливаем флаг isSubmitted в false
            dispose(); // Закрываем диалоговое окно
        });
        submitPanel.add(cancelButton);

        return submitPanel;
    }

    public boolean isSubmitted() {
        return isSubmitted;
    }

    public String getDirectionSelector() {
        if (isSelectedTab) {
            return (String) directionSelectorSecondTab.getSelectedItem();
        } else {
            return (String) directionSelectorFirstTab.getSelectedItem();
        }
    }

    public Integer getSelectorCountZabStup() {
        return (Integer) selectorFirstTabZabStupen.getSelectedItem();
    }

    public String getPathSaveMono() {
        return pathSaveMono.getText();
    }

    public String getPathSaveAuto() {
        return pathSaveAuto.getText();
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

    public boolean isSelectedValue() {
        return selectedValue;
    }

    public String getOtstupValue() {
        return otstupValue + "";
    }

    public String getShirinaPloshadki() {
        return shirinaPloshadki + "";
    }
}
