package com.sapnu.tuitiondays.tuition_day_list;

import com.sapnu.tuitiondays.entity.TuitionDateObject;

public interface UpdateTuitionDayDialogCallBacks {
    void dialogCancelPressed();
    void dialogUpdatePressed(TuitionDateObject previous, TuitionDateObject current);
}
