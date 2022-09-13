package it.meteoapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import it.meteoapp.R
import it.meteoapp.model.Location
import it.meteoapp.model.LocationsHolder

class DetailLocationFragment : Fragment() {
    private var mLocation: Location? = null
    private var mNameTextView: TextView? = null
    private var mImageView: ImageView? = null
    private var mDescriptionTextView: TextView? = null
    private var mTempTextView: TextView? = null
    private var mMinMaxTempTextView: TextView? = null
    private var mPressureTextView: TextView? = null
    private var mHumidityTextView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val locationId = requireArguments().getSerializable(ARG_LOCATION_ID) as String?
        mLocation = locationId?.let { activity?.let { it1 -> LocationsHolder.get(it1)?.getLocation(it) } }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_detail_location, container, false)
        mNameTextView = v.findViewById(R.id.name_textView)
        mImageView = v.findViewById(R.id.image_detail)
        mDescriptionTextView = v.findViewById(R.id.description_textView)
        mTempTextView = v.findViewById(R.id.temp_textView)
        mMinMaxTempTextView = v.findViewById(R.id.min_max_temp_textView)
        mPressureTextView = v.findViewById(R.id.pressure_textView)
        mHumidityTextView = v.findViewById(R.id.humidity_textView)
        mNameTextView?.setText(mLocation!!.name)
        val id = requireContext().resources.getIdentifier(
            mLocation!!.weather_icon,
            "drawable",
            requireContext().packageName
        )
        mImageView?.setImageResource(id)
        mDescriptionTextView?.setText(mLocation!!.weather_descr)
        mTempTextView?.setText(mLocation!!.temp.toString() + "°")
        mMinMaxTempTextView?.setText(mLocation!!.temp_min.toString() + "°" + " | " + mLocation!!.temp_max + "°")
        mPressureTextView?.setText(mLocation!!.pressure.toString() + " Pa")
        mHumidityTextView?.setText(mLocation!!.humidity.toString() + "%")
        return v
    }

    fun newInstance(locationId: String?): Fragment {
        val args = Bundle()
        args.putSerializable(ARG_LOCATION_ID, locationId)

        val fragment = DetailLocationFragment()
        fragment.arguments = args
        return fragment
    }

    companion object {
        private const val ARG_LOCATION_ID = "location_id"
        fun newInstance(locationId: String?): DetailLocationFragment {
            val args = Bundle()
            args.putSerializable(ARG_LOCATION_ID, locationId)
            val fragment = DetailLocationFragment()
            fragment.arguments = args
            return fragment
        }
    }
}