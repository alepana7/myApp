package it.meteoapp.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.*
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import it.meteoapp.Constants
import it.meteoapp.DBManager
import it.meteoapp.OnDialogResultListener
import it.meteoapp.R
import it.meteoapp.activities.DetailActivity
import it.meteoapp.model.Location
import it.meteoapp.model.LocationsHolder
import it.meteoapp.tasks.GetByCoordsTask
import it.meteoapp.tasks.GetByNameTask
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.config.LocationAccuracy
import io.nlopez.smartlocation.location.config.LocationParams
import java.util.concurrent.ExecutionException

class ListFragment : Fragment(), OnDialogResultListener {
    private var mLocationRecyclerView: RecyclerView? = null
    private var mAdapter: LocationAdapter? = null
    private var dbManager: DBManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        dbManager = context?.let { DBManager.init(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_list, container, false)
        mLocationRecyclerView = view.findViewById(R.id.recycler_view)
        mLocationRecyclerView?.setLayoutManager(LinearLayoutManager(activity))
        val locations: MutableList<Location> = LocationsHolder.get(
            requireActivity().applicationContext
        )!!.locations
        mAdapter = LocationAdapter(locations)
        mLocationRecyclerView?.setAdapter(mAdapter)
        return view
    }

    // Menu
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.fragment_list, menu)
    }

    /***************************************************************************************
     * LOCATION INSERTION                                     *
     */
    override fun onDialogResult(result: String?) {
        val getByNameTask = GetByNameTask()
        var location: Location? = null
        try {
            location = getByNameTask.execute(result).get()
        } catch (e: ExecutionException) {
            e.printStackTrace()
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        if (location == null) {
            Toast.makeText(
                activity,
                "Inexistent location",
                Toast.LENGTH_SHORT
            )
                .show()
            return
        }
        val finalLocation: Location = location
        Thread {
            if (dbManager?.locationDao()?.getLocation(finalLocation.name) != null) {
                Handler(Looper.getMainLooper()).post(Runnable {
                    val toast: Toast = Toast.makeText(
                        activity,
                        "Location already present",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                })
            } else {
                val id: Long = dbManager!!.locationDao()!!.insertLocation(finalLocation)
                Handler(Looper.getMainLooper()).post(Runnable {
                    val toast: Toast = Toast.makeText(
                        activity,
                        "Location added",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                    mAdapter!!.mLocations.add(finalLocation)
                    mAdapter!!.notifyItemInserted(id.toInt())
                })
            }
        }.start()
    }

    private fun showDialogAndGetresult(
        title: String,
        message: String?,
        initialText: String,
        listener: OnDialogResultListener?
    ) {
        val editText = EditText(context)
        editText.setText(initialText)
        AlertDialog.Builder(context)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(
                "Add",
                DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
                    if (listener != null) if (editText.getText()
                            .toString() != ""
                    ) listener.onDialogResult(editText.getText().toString())
                })
            .setView(editText)
            .show()
    }

    /***************************************************************************************
     * GPS PERMISSION AND LOCATION INSERTION                         *
     */
    private fun requestPermission() {
        if (ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this.requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        } else {
            startLocationListener()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            0 -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) startLocationListener()
                return
            }
        }
    }

    private fun startLocationListener() {
        if (!SmartLocation.with(activity).location().state().isGpsAvailable()) {
            val toast: Toast = Toast.makeText(
                activity,
                "GPS is disabled",
                Toast.LENGTH_SHORT
            )
            toast.show()
        } else {
            val builder: LocationParams.Builder = LocationParams.Builder()
                .setAccuracy(LocationAccuracy.HIGH)
            SmartLocation.with(activity).location().oneFix().config(builder.build())
                .start { location ->
                    Log.i(Constants.GPS, "Location$location")
                    val getByCoordsTask = GetByCoordsTask()
                    var loc: Location = Location()
                    try {
                        loc =
                            getByCoordsTask.execute(location.getLatitude(), location.getLongitude())
                                .get()!!
                        val finalLoc = loc
                        Thread {
                            if (dbManager?.locationDao()?.getLocation(
                                    finalLoc!!.name
                                ) != null
                            ) {
                                Handler(Looper.getMainLooper()).post(Runnable {
                                    val toast: Toast = Toast.makeText(
                                        activity,
                                        "Location already present",
                                        Toast.LENGTH_SHORT
                                    )
                                    toast.show()
                                })
                            } else {
                                val id: Long? = dbManager?.locationDao()?.insertLocation(finalLoc)
                                Handler(Looper.getMainLooper()).post(Runnable {
                                    val toast: Toast = Toast.makeText(
                                        activity,
                                        "Location added",
                                        Toast.LENGTH_SHORT
                                    )
                                    toast.show()
                                    mAdapter!!.mLocations.add(finalLoc)
                                    id?.let { mAdapter!!.notifyItemInserted(it.toInt()) }
                                })
                            }
                        }.start()
                    } catch (e: ExecutionException) {
                        e.printStackTrace()
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                    val toast: Toast = Toast.makeText(
                        activity,
                        location.getLatitude().toString() + " " + location.getLongitude(),
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                }
        }
    }

    private fun addGpsLocation() {
        if (ContextCompat.checkSelfPermission(
                this.requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.i(Constants.GPS, "Permission not granted")
            requestPermission()
        } else {
            Log.i(Constants.GPS, "Permission granted")
            startLocationListener()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_add -> {
                showDialogAndGetresult("Add location", null, "", this)
                true
            }
            R.id.menu_gps -> {
                addGpsLocation()
                super.onOptionsItemSelected(item)
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    // Holder
    private inner class LocationHolder(inflater: LayoutInflater, parent: ViewGroup?) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.list_item, parent, false)),
        View.OnClickListener, View.OnLongClickListener {
        private val mNameTextView: TextView
        private val mDegreeTextView: TextView
        private val mImageView: ImageView
        private var mLocation: Location? = null
        override fun onClick(view: View) {
            val intent: Intent = DetailActivity.newIntent(activity, mLocation!!.id)
            startActivity(intent)
        }

        override fun onLongClick(v: View): Boolean {
            val index = mAdapter!!.mLocations.indexOf(mLocation)
            mAdapter!!.mLocations.remove(mLocation)
            mAdapter!!.notifyItemRemoved(index)
            Thread { dbManager?.locationDao()?.deleteLocation(mLocation) }.start()
            return true
        }

        fun bind(location: Location?) {
            mLocation = location
            mNameTextView.setText(mLocation!!.name)
            mDegreeTextView.setText(mLocation!!.temp.toString() + "°")
            val id = context!!.resources.getIdentifier(
                mLocation!!.weather_icon,
                "drawable",
                context!!.packageName
            )
            mImageView.setImageResource(id)
        }

        init {
            itemView.setOnClickListener(this)
            itemView.setOnLongClickListener(this)
            mNameTextView = itemView.findViewById<TextView>(R.id.name)
            mDegreeTextView = itemView.findViewById<TextView>(R.id.degrees)
            mImageView = itemView.findViewById<ImageView>(R.id.image)
        }
    }

    // Adapter
    private inner class LocationAdapter(val mLocations: MutableList<Location>) :
        RecyclerView.Adapter<LocationHolder?>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationHolder {
            val layoutInflater: LayoutInflater = LayoutInflater.from(activity)
            return LocationHolder(layoutInflater, parent)
        }

        override fun onBindViewHolder(holder: LocationHolder, position: Int) {
            val location = mLocations[position]
            holder.bind(location)
        }

        override fun getItemCount(): Int {
            return mLocations.size
        }
    }
}