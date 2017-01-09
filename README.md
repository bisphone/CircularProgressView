# CircularProgressView

customize it as you want.

Via XML :

    <declare-styleable name="Circular">
        <attr name="Circular_progress" format="float" />
        <attr name="Circular_max" format="integer" />

        <attr name="Circular_unfinished_color" format="color" />
        <attr name="Circular_finished_color" format="color" />

        <attr name="Circular_finished_stroke_width" format="dimension" />
        <attr name="Circular_unfinished_stroke_width" format="dimension" />
        <attr name="Circular_indeterminate_size" format="dimension" />
        <attr name="Circular_indeterminate" format="boolean" />

        <attr name="Circular_background_color" format="color" />

        <attr name="Circular_starting_degree" format="integer" />
        <attr name="Circular_animation_duration" format="integer" />
        <attr name="Circular_progress_num" format="integer" />
    </declare-styleable>
    
You can use it as :

      <shayan.progressview.CircularProgress
        xmlns:circular="http://schemas.android.com/apk/res-auto"
        android:id="@+id/row_circularProgress"
        android:layout_width="48dp"
        android:layout_height="48dp"
        circular:Circular_finished_stroke_width="4dp"
        circular:Circular_progress_num="3"
        circular:Circular_indeterminate= "false"
        circular:Circular_unfinished_stroke_width="4dp" />
    

or you can customize it from java code like :

      CircularProgress circularProgress = (CircularProgress) itemView.findViewById(R.id.row_circularProgress);
      circularProgress.setProgress(50);
      circularProgress.setIndeterminate(false);
      circularProgress.setProgressNum(2);
      circularProgress.setUnfinishedStrokeColor(ContextCompat.getColor(context, android.R.color.transparent));
      .....
    

