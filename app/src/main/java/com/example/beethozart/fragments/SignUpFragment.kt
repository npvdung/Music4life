package com.example.beethozart.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import com.example.beethozart.R
import com.example.beethozart.databinding.FragmentSignUpBinding
import com.example.beethozart.network.Api
import com.example.beethozart.network.UserFromServer
import com.example.beethozart.network.UserSignUpProperty
import retrofit2.Call
import retrofit2.Response

/**
 * A simple [Fragment] subclass.
 * Use the [SignUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SignUpFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentSignUpBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_sign_up, container, false)

        binding.buttonSignUp.setOnClickListener {
            val userName = binding.userNameTextField.editText?.text.toString()
            val password = binding.passwordTextField.editText?.text.toString()
            val confirmPassword = binding.passwordConfirmTextField?.editText?.text.toString()
            val user = UserSignUpProperty(userName, password, confirmPassword)
            signUpUser(it, user)

            binding.userNameTextField.editText?.text?.clear()
            binding.passwordTextField.editText?.text?.clear()
            binding.passwordConfirmTextField.editText?.text?.clear()
        }


        return binding.root
    }

    private fun checkConfirm(password: String, confirmPassword: String): Boolean {
        val check = confirmPassword.compareTo(password)
        if (check == 0) return true
        return false
    }

    private fun isPasswordInvalid(username: String, password: String): Boolean {
        if (password.length < 3 || username.length < 3) {
            return false
        }
        return true
    }

    private fun signUpUser(view: View?, user: UserSignUpProperty) {
        if (!checkConfirm(user.password, user.confirmPassword)) {
            return
        }
        if (!isPasswordInvalid(user.username, user.password)) {
            return
        }

        postToServer(view, user)
    }

    private fun postToServer(view: View?, user: UserSignUpProperty) {
        Log.d("aaa", "post")
        Api.retrofitService.signUpUser(user).enqueue(object : retrofit2.Callback<UserFromServer> {
            override fun onResponse(
                call: Call<UserFromServer>,
                response: Response<UserFromServer>
            ) {
                val httpCode = response.code()
                if (httpCode == 400) {
                    Toast.makeText(context, "Username has existed!", Toast.LENGTH_LONG).show()
                } else if (httpCode == 200) {
                    Toast.makeText(context, "Create successful!", Toast.LENGTH_SHORT).show()
                    if (view != null) {
                        Navigation.findNavController(view)
                            .navigate(R.id.action_signUpFragment_to_signInFragment)
                    }
                }
            }

            override fun onFailure(call: Call<UserFromServer>, t: Throwable) {
                Toast.makeText(context, "Error", Toast.LENGTH_LONG).show()
            }
        })
    }

}