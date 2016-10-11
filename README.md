# SegmentedSeekBar
###a segmented view like o——o——o
minSdkVersion >= 14 (⊙o⊙)…
------
![gif](https://github.com/bean883/SegmentedSeekBar/blob/master/pic/demo.gif)
##how to use
```
compile 'cn.bingod:segmentedseekbar:1.0.1'
```
```
<cn.bingod.widget.segmentedseekbar.SegmentedSeekBar xmlns:ssb="http://schemas.android.com/apk/res-auto"
        android:id="@+id/ssb"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:padding="0dp"
        ssb:line_color="#ccc"
        ssb:line_weight="2dp"
        ssb:orientation="horizontal"
        ssb:segmentCount="5"
        ssb:segmentDrawable="@drawable/dot"
        ssb:touchDrawable="@drawable/move_dot" />
```
