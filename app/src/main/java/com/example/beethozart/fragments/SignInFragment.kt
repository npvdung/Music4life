package com.example.beethozart.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.example.beethozart.R
import com.example.beethozart.databases.SongDatabase
import com.example.beethozart.databinding.FragmentSignInBinding
import com.example.beethozart.network.Api
import com.example.beethozart.network.UserFromServer
import com.example.beethozart.network.UserSignInProperty
import com.example.beethozart.viewmodels.SignInViewModel
import com.example.beethozart.viewmodels.factories.SignInViewModelFactory
import retrofit2.Call
import retrofit2.Response

class SignInFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentSignInBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_sign_in, container, false)


        val application = requireNotNull(this.activity).application
        val dataSource = SongDatabase.getInstance(application).songDatabaseDao

        val viewModelFactory = SignInViewModelFactory(dataSource, application)

        val viewModel =
            ViewModelProviders.of(this, viewModelFactory).get(SignInViewModel::class.java)

        binding.buttonSignIn.setOnClickListener {
            val username = binding.userNameTextField.editText?.text.toString()
            val password = binding.passwordTextField.editText?.text.toString()
            val user = UserSignInProperty(username, password)
            signInUser(it, user, viewModel)
            binding.userNameTextField.editText?.text?.clear()
            binding.passwordTextField.editText?.text?.clear()
        }

        binding.extendedFab.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_signInFragment_to_signUpFragment)
        }
        binding.lifecycleOwner = this

        viewModel.userGet.observe(viewLifecycleOwner, Observer {
            if (it.size == 1) {
                Log.d("aaa", it[0].username)
            }
        })

        return binding.root
    }

    private fun checkValid(user: UserSignInProperty): Boolean {
        if (user.username.length < 6 || user.password.length < 8) {
            return false
        }
        return true
    }

    private fun signInUser(view: View?, user: UserSignInProperty, viewModel: SignInViewModel) {
        if (!checkValid(user)) return

        Api.retrofitService.signInUser(user).enqueue(object : retrofit2.Callback<UserFromServer> {
            override fun onResponse(
                call: Call<UserFromServer>,
                response: Response<UserFromServer>
            ) {
                val httpCode = response.code()
                if (httpCode == 401) {
                    Toast.makeText(context, "Username or Password is wrong!", Toast.LENGTH_LONG)
                        .show()
                } else if (httpCode == 200) {
                    Toast.makeText(context, "Sign In Successful!", Toast.LENGTH_SHORT).show()
                    viewModel.addUser(user.username, user.password)
                    if (view != null) {
                        Navigation.findNavController(view)
                            .navigate(R.id.action_signInFragment_to_onlineHomeFragment)
                    }
                }
            }

            override fun onFailure(call: Call<UserFromServer>, t: Throwable) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }

        })
    }

}