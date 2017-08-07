package sega.fastnetwork.test.fragment


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView

import sega.fastnetwork.test.R


/**
 * Created by Admin on 5/25/2016.
 */
class TabGeoCone : Fragment() {

    internal var radius: EditText? = null
    internal var height: EditText? = null
    internal var lukis: EditText? = null
    internal var tv_vol: TextView? = null
    internal var tv_luas: TextView? = null
    internal var tv_kel: TextView? = null
    internal var hasil: Button? = null

    internal var vol: Double = 0.toDouble()
    internal var luas: Double = 0.toDouble()
    internal var luasper: Double = 0.toDouble()

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val v = inflater!!.inflate(R.layout.fragment_tab1, container, false)

        return v
    }


    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
