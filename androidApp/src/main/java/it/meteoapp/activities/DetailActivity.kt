package it.meteoapp.activities

import android.content.Context
import it.meteoapp.fragments.DetailLocationFragment
import android.content.Intent
import androidx.fragment.app.Fragment

class DetailActivity : SingleFragmentActivity() {
    override fun createFragment(): Fragment {
        val locationId = intent.getSerializableExtra(EXTRA_LOCATION_ID) as String?
        return DetailLocationFragment().newInstance(locationId)
    }

    companion object {
        private const val EXTRA_LOCATION_ID = "it.meteoapp.location_id"
        @JvmStatic
        fun newIntent(packageContext: Context?, locationId: String?): Intent {
            val intent = Intent(packageContext, DetailActivity::class.java)
            intent.putExtra(EXTRA_LOCATION_ID, locationId)
            return intent
        }
    }
}