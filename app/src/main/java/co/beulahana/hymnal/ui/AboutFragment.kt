package co.beulahana.hymnal.ui


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment

import co.beulahana.hymnal.R
import kotlinx.android.synthetic.main.fragment_about.view.*
import android.content.Intent
import android.net.Uri




/**
 * A simple [Fragment] subclass.
 *
 */
class AboutFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        activity?.setTitle("About")
        setHasOptionsMenu(true)

        val view= inflater.inflate(R.layout.fragment_about, container, false)
        init(view)
        return view
    }

    private fun init(view: View) {
        view.send_feedback.setOnClickListener {
            val subject = context?.getString(R.string.app_name)
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "plain/text"
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf("buelersandra@gmail.com"))
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, "")

            /* Send it off to the Activity-Chooser */
            startActivity(Intent.createChooser(intent, "Send Email"))

        }

    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        menu?.clear()
    }


}
