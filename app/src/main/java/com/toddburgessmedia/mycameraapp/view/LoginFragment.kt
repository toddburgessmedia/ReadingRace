package com.toddburgessmedia.mycameraapp.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.toddburgessmedia.mycameraapp.CameraViewModel
import com.toddburgessmedia.mycameraapp.R
import com.toddburgessmedia.mycameraapp.model.User
import kotlinx.android.synthetic.main.fragment_login.*
import org.koin.android.viewmodel.ext.android.sharedViewModel

class LoginFragment : Fragment() {

    var name : String? = ""
    var email : String? = ""
    var uid : String? = ""

    val viewModel : CameraViewModel by sharedViewModel()

    companion object {
        fun newInstance(bundle: Bundle) : LoginFragment {

            val loginFragment = LoginFragment()
            loginFragment.arguments = bundle
            return loginFragment

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_login,container,false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bundle = arguments
        name = bundle?.getString("name")
        email = bundle?.getString("email")
        uid = bundle?.getString("uid")

        login_name.setText(name)
        login_email.setText(email)

        login_button.setOnClickListener {
            val user = User(uid,email,name,0)
            viewModel.createUser(user)
        }
    }
}