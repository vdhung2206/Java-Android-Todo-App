package com.example.todoapp.Adapters;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import com.example.todoapp.Entity.GhiChu;
import com.example.todoapp.FirstFragment;
import com.example.todoapp.MySpinner;
import com.example.todoapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyDialog extends DialogFragment {
    public interface OnDialogDismissListener {
        void onDismiss();
    }
    public interface DialogButtonListener {
        void onSaveClicked();
        void onCancelClicked();
        void onDeleteClicked();
    }
    public interface Action {
        void setupDateTime();
    }
    private OnDialogDismissListener dismissListener;
    private DialogButtonListener dialogListener;
    private Action action;
    private View dialogView;
    private Button buttonSave;
    private Button buttonCancel;
    private Button buttonDelete;
    private MySpinner spinnerDate;
    private MySpinner spinnerTime;
    private MySpinner spinnerRepeat;
    private boolean firstOpenDialog = true;
    private boolean hasNotify = false;
    private boolean timeSeted = true;
    public Calendar selectedDate = Calendar.getInstance();
    public Calendar selectedTime = Calendar.getInstance();
    public int repeatOption;
    private AlertDialog alertDialog;
    private ArrayAdapter<String> dateAdapter;
    private ArrayAdapter<String> timeAdapter;
    private ArrayAdapter<String> repeatAdapter;
    private View view;
    private TextView spinnerDateTextView;
    private TextView spinnerTimeTextView;
    private TextView spinnerRepeatTextView;
    private TextView titleTextView;
    public static MyDialog newInstance() {
        return new MyDialog();
    }
    public static MyDialog newInstance(OnDialogDismissListener dismissListener) {
        MyDialog fragment = new MyDialog();
        fragment.dismissListener = dismissListener;
        return fragment;
    }

    @NonNull
    public AlertDialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialogView = getLayoutInflater().inflate(R.layout.them_loi_nhac, null);
        buttonSave = dialogView.findViewById(R.id.buttonSave);
        buttonCancel = dialogView.findViewById(R.id.buttonCancel);
        buttonDelete = dialogView.findViewById(R.id.buttonDelete);
        titleTextView = dialogView.findViewById(R.id.dialogTitle);
        builder.setView(dialogView);
        alertDialog = builder.create();

        Drawable drawable = ContextCompat.getDrawable(getContext(), R.drawable.popup);
        alertDialog.getWindow().setBackgroundDrawable(drawable);


        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogListener != null) {
                    dialogListener.onSaveClicked();
                }
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogListener != null) {
                    dialogListener.onCancelClicked();
                }
            }
        });
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (dialogListener != null) {
                    dialogListener.onDeleteClicked();
                }
            }
        });
        alertDialog.show();
        alertDialog.getWindow().setLayout(850, 870);
        setupDateSpinner();
        setupTimeSpinner();
        action.setupDateTime();
        setupRepeatSpinner();
        return alertDialog;
    }
    public void setTitle(String title){
        this.titleTextView.setText(title);
    }
    public void setDialogListener(DialogButtonListener listener) {
        this.dialogListener = listener;
    }
    public void setAction(Action action){
        this.action = action;
    }
    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (dismissListener != null) {
            dismissListener.onDismiss();
        }
    }
    private void setupDateSpinner() {
        ArrayList<String> dateOptions = new ArrayList<>();
        dateOptions.add("Hôm nay");
        dateOptions.add("Ngày mai");
        dateOptions.add("Một tuần sau");
        dateOptions.add("Chọn ngày...");

        Calendar today = Calendar.getInstance();

        Calendar tomorrow = (Calendar) today.clone();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);

        Calendar nextWeek = (Calendar) today.clone();
        nextWeek.add(Calendar.DAY_OF_MONTH, 7);

        int todayYear = today.get(Calendar.YEAR);
        int todayMonth = today.get(Calendar.MONTH) + 1;
        int todayDay = today.get(Calendar.DAY_OF_MONTH);

        int tomorrowYear = tomorrow.get(Calendar.YEAR);
        int tomorrowMonth = tomorrow.get(Calendar.MONTH) + 1;
        int tomorrowDay = tomorrow.get(Calendar.DAY_OF_MONTH);

        int nextWeekYear = nextWeek.get(Calendar.YEAR);
        int nextWeekMonth = nextWeek.get(Calendar.MONTH) + 1;
        int nextWeekDay = nextWeek.get(Calendar.DAY_OF_MONTH);

        dateAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, dateOptions) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                view = super.getView(position, convertView, parent);
                spinnerDateTextView = view.findViewById(android.R.id.text1);
                String item = dateOptions.get(position);
                spinnerDateTextView.setText(selectedDate.get(Calendar.DAY_OF_MONTH) + " tháng " + (selectedDate.get(Calendar.MONTH) + 1));
                return view;
            }
        };

        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerDate = alertDialog.findViewById(R.id.spinnerDate);
        spinnerDate.setAdapter(dateAdapter);

        spinnerDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedOption = dateOptions.get(position);
                if(firstOpenDialog){
                    firstOpenDialog = false;
                } else if (hasNotify) {
                    hasNotify = false;
                }
                else {
                    if (selectedOption.equals("Hôm nay")) {
                        selectedDate.set(todayYear, todayMonth - 1, todayDay);
                        Calendar currentTime = Calendar.getInstance();
                        currentTime.add(Calendar.HOUR_OF_DAY, 3);
                        int minute = currentTime.get(Calendar.MINUTE);

                        if (minute < 15) {
                            currentTime.set(Calendar.MINUTE, 0); // Làm tròn xuống 0 phút
                        } else if (minute < 30) {
                            currentTime.set(Calendar.MINUTE, 30); // Làm tròn lên 30 phút
                        } else if (minute < 45) {
                            currentTime.set(Calendar.MINUTE, 30); // Làm tròn xuống 30 phút
                        } else {
                            currentTime.add(Calendar.HOUR, 1);
                            currentTime.set(Calendar.MINUTE, 0);
                            if (currentTime.get(Calendar.HOUR_OF_DAY) == 0) {
                                // Nếu giờ là 0, chỉ đặt ngày là ngày tiếp theo và giờ là 8:00
                                currentTime.add(Calendar.DAY_OF_YEAR, 1);
                                currentTime.set(Calendar.HOUR_OF_DAY, 8);
                            }
                        }
                        if (currentTime.get(Calendar.DAY_OF_YEAR) > selectedDate.get(Calendar.DAY_OF_YEAR)) {
                            Calendar toBeSelectedTime = Calendar.getInstance();
                            toBeSelectedTime.add(Calendar.HOUR_OF_DAY,1);
                            if(toBeSelectedTime.get(Calendar.DAY_OF_YEAR) > selectedTime.get(Calendar.DAY_OF_YEAR)){
                                selectedTime.set(Calendar.HOUR_OF_DAY,23);
                                selectedTime.set(Calendar.MINUTE,59);
                            } else {
                                selectedTime = toBeSelectedTime;
                            }
                        } else {
                            selectedTime = currentTime; // Nếu không sang ngày mới, chỉ cập nhật thời gian.
                        }
                        timeAdapter.notifyDataSetChanged();
                    } else if (selectedOption.equals("Ngày mai")) {
                        selectedDate.set(tomorrowYear, tomorrowMonth - 1, tomorrowDay);
                    } else if (selectedOption.equals("Một tuần sau")) {
                        selectedDate.set(nextWeekYear, nextWeekMonth - 1, nextWeekDay);
                    } else if (selectedOption.equals("Chọn ngày...")) {
                        openCustomDatePicker();
                    }
                }
                dateAdapter.notifyDataSetChanged();
            }
            private void openCustomDatePicker() {
                Calendar today = Calendar.getInstance();
                int year = today.get(Calendar.YEAR);
                int month = today.get(Calendar.MONTH);
                int day = today.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        // Cập nhật ngày tháng đã chọn vào biến selectedDate
                        selectedDate.set(year, month, dayOfMonth);
                        spinnerDateTextView.setText(selectedDate.get(Calendar.DAY_OF_MONTH) + " tháng " + (selectedDate.get(Calendar.MONTH) + 1));
                        dateAdapter.notifyDataSetChanged();
                    }
                }, year, month, day);
                datePickerDialog.getDatePicker().setMinDate(today.getTimeInMillis());
                datePickerDialog.show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }
    private void setupTimeSpinner() {
        ArrayList<String> timeOptions = new ArrayList<>();
        timeOptions.add("Sáng (8:00)");
        timeOptions.add("Chiều (13:00)");
        timeOptions.add("Tối (18:00)");
        timeOptions.add("Đêm (22:00)");
        timeOptions.add("Chọn thời gian...");

        Calendar sang = Calendar.getInstance();
        sang.set(Calendar.HOUR_OF_DAY, 8);
        sang.set(Calendar.MINUTE, 0);

        Calendar chieu = Calendar.getInstance();
        chieu.set(Calendar.HOUR_OF_DAY, 13);
        chieu.set(Calendar.MINUTE, 0);

        Calendar toi = Calendar.getInstance();
        toi.set(Calendar.HOUR_OF_DAY, 18);
        toi.set(Calendar.MINUTE, 0);

        Calendar dem = Calendar.getInstance();
        dem.set(Calendar.HOUR_OF_DAY, 22);
        dem.set(Calendar.MINUTE, 0);

        timeAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, timeOptions) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                view = super.getView(position, convertView, parent);
                spinnerTimeTextView = view.findViewById(android.R.id.text1);
                String item = timeOptions.get(position);
                String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedTime.get(Calendar.HOUR_OF_DAY), selectedTime.get(Calendar.MINUTE));
                spinnerTimeTextView.setText(formattedTime);
                return view;
            }

            @Override
            public boolean isEnabled(int position) {
                boolean isToday = isDateToday(selectedDate);
                if (isToday) {
                    String item = timeOptions.get(position);
                    String timeText = ""; // Chuyển item thành định dạng thời gian
                    if (item.equals("Sáng (8:00)")) {
                        timeText = "08:00";
                    } else if (item.equals("Chiều (13:00)")) {
                        timeText = "13:00";
                    } else if (item.equals("Tối (18:00)")) {
                        timeText = "18:00";
                    } else if (item.equals("Đêm (22:00)")) {
                        timeText = "22:00";
                    }
                    return !isTimePassed(timeText);
                }
                return true;
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                spinnerTimeTextView = view.findViewById(android.R.id.text1);
                boolean isToday = isDateToday(selectedDate);
                if (isToday) {
                    String item = timeOptions.get(position);
                    String timeText = ""; // Chuyển item thành định dạng thời gian
                    if (item.equals("Sáng (8:00)")) {
                        timeText = "08:00";
                    } else if (item.equals("Chiều (13:00)")) {
                        timeText = "13:00";
                    } else if (item.equals("Tối (18:00)")) {
                        timeText = "18:00";
                    } else if (item.equals("Đêm (22:00)")) {
                        timeText = "22:00";
                    }
                    if (isTimePassed(timeText)) {
                        spinnerTimeTextView.setTextColor(Color.GRAY);
                    } else {
                        spinnerTimeTextView.setTextColor(Color.BLACK);
                    }
                } else {
                    spinnerTimeTextView.setTextColor(Color.BLACK);
                }
                return view;
            }
        };
        timeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerTime = alertDialog.findViewById(R.id.spinnerTime);
        spinnerTime.setAdapter(timeAdapter);

        spinnerTime.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(firstOpenDialog){
                    firstOpenDialog = false;
                } else if (hasNotify) {
                    hasNotify = false;
                }else if (timeSeted){
                    timeSeted = false;
                } else {
                    String selectedOption = timeOptions.get(position);
                    if (selectedOption.equals("Sáng (8:00)")) {
                        selectedTime = sang;
                    } else if (selectedOption.equals("Chiều (13:00)")) {
                        selectedTime = chieu;
                    } else if (selectedOption.equals("Tối (18:00)")) {
                        selectedTime = toi;
                    } else if (selectedOption.equals("Đêm (22:00)")) {
                        selectedTime = dem;
                    } else if (selectedOption.equals("Chọn thời gian...")) {
                        openCustomTimePicker();
                    }
                }
                timeAdapter.notifyDataSetChanged();
                Log.e(selectedTime.get(Calendar.HOUR_OF_DAY)+"","Gio");
                Log.e(selectedTime.get(Calendar.MINUTE)+"","Phut");
            }
            public void openCustomTimePicker() {
                Calendar today = Calendar.getInstance();
                int hour = today.get(Calendar.HOUR_OF_DAY);
                int minute = today.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        selectedTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        selectedTime.set(Calendar.MINUTE, minute);

                        String formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedTime.get(Calendar.HOUR_OF_DAY), selectedTime.get(Calendar.MINUTE));
                        spinnerTimeTextView.setText(formattedTime);
                        boolean isToday = isDateToday(selectedDate);
                        if (isToday) {
                            if (isTimePassed(formattedTime)) {
                                Toast.makeText(getContext(), "Thời gian đã qua, vui lòng chọn lại!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        timeAdapter.notifyDataSetChanged();
                    }
                }, hour, minute, true);
                timePickerDialog.show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
    }
    private boolean isDateToday(Calendar selectedDate) {
        Calendar today = Calendar.getInstance();

        return selectedDate.get(Calendar.YEAR) == today.get(Calendar.YEAR)
                && selectedDate.get(Calendar.MONTH) == today.get(Calendar.MONTH)
                && selectedDate.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);
    }
    private boolean isTimePassed(String selectedTimeStr) {
        Calendar currentTime = Calendar.getInstance();

        Calendar selectedTime = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
        try {
            selectedTime.setTime(sdf.parse(selectedTimeStr));
        } catch (ParseException e) {
            // Xử lý lỗi chuyển đổi chuỗi thời gian
            e.printStackTrace();
            return false; // Hoặc bạn có thể xử lý lỗi theo cách khác tùy theo yêu cầu
        }

        // Bỏ đi thông tin ngày và tháng
        currentTime.set(1, 0, 0); // 1 = năm, 0 = tháng, 0 = ngày
        selectedTime.set(1, 0, 0); // 1 = năm, 0 = tháng, 0 = ngày

        // So sánh thời gian đã chọn với thời gian hiện tại
        // Thời gian đã chọn đã qua
        return selectedTime.before(currentTime);

        // Thời gian đã chọn chưa qua
    }
    private void setupRepeatSpinner() {
        ArrayList<String> repeatOptions = new ArrayList<>();
        repeatOptions.add("Không lặp lại");
        repeatOptions.add("Lặp lại hằng ngày");
        repeatOptions.add("Lặp lại hằng tuần");
        repeatOptions.add("Lặp lại hằng tháng");
        repeatOptions.add("Lặp lại hằng năm");

        repeatAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, repeatOptions) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                view = super.getView(position, convertView, parent);
                spinnerRepeatTextView = view.findViewById(android.R.id.text1);
                // Xử lý việc hiển thị dựa trên lựa chọn
                String item = repeatOptions.get(position);
                spinnerRepeatTextView.setText(item);
                return view;
            }
        };

        repeatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerRepeat = alertDialog.findViewById(R.id.spinnerRepeat);
        spinnerRepeat.setAdapter(repeatAdapter);

        spinnerRepeat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selectedOption = repeatOptions.get(position);

                if (selectedOption.equals("Không lặp lại")) {
                    repeatOption = 0;
                } else if (selectedOption.equals("Lặp lại hằng ngày")) {
                    repeatOption = 1;
                } else if (selectedOption.equals("Lặp lại hằng tuần")) {
                    repeatOption = 2;
                } else if (selectedOption.equals("Lặp lại hằng tháng")) {
                    repeatOption = 3;
                } else if (selectedOption.equals("Lặp lại hằng năm")) {
                    repeatOption = 4;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                if (spinnerRepeat.getSelectedItemPosition() >= 0) {
                    String selectedItem = repeatOptions.get(spinnerRepeat.getSelectedItemPosition());
                    // Xử lý việc chọn lại mục hiện tại (nếu cần)
                    Toast.makeText(getContext(), "Reselected item: " + selectedItem, Toast.LENGTH_SHORT).show();
                }
            }
        });
        spinnerRepeat.setSelection(repeatOption);

    }
    public void setSelectedDate(Date selectedDate){
        this.selectedDate.setTime(selectedDate);
    }
    public void setSelectedTime(Date selectedTime){
        this.selectedTime.setTime(selectedTime);
    }
    public void setFirstOpenDialog(boolean firstOpenDialog){
        this.firstOpenDialog = firstOpenDialog;
    }
    public void setHasNotify(boolean hasNotify){
        this.hasNotify = hasNotify;
    }
    public void setTimeSeted(boolean timeSeted){
        this.timeSeted = timeSeted;
    }
    public boolean getHasNotify(){
        return hasNotify;
    }
    public Calendar getSelectedDate(){
        return this.selectedDate;
    }
    public Calendar getSelectedTime(){
        return this.selectedTime;
    }
    public int getRepeat(){
        return this.repeatOption;
    }
    public void showButtonDelete(){
        this.buttonDelete.setVisibility(View.VISIBLE);
    }
}
