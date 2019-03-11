package com.sapnu.tuitiondays.tuition_day_list;

import com.sapnu.tuitiondays.entity.TuitionDateObject;

public interface AddTuitionDayDialogCallBacks {
    void dialogCancelPressed();
    void dialogSavePressed(TuitionDateObject date);
}
