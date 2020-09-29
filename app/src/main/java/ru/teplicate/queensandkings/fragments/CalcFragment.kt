package ru.teplicate.queensandkings.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import ru.teplicate.queensandkings.R
import ru.teplicate.queensandkings.databinding.CalcFragmentBinding
import ru.teplicate.queensandkings.util.Util
import ru.teplicate.queensandkings.view_models.CalcState
import ru.teplicate.queensandkings.view_models.CalcViewModel

class CalcFragment : Fragment() {

    private lateinit var binding: CalcFragmentBinding
    private lateinit var viewModel: CalcViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate<CalcFragmentBinding>(
            inflater,
            R.layout.calc_fragment,
            container,
            false
        )

        binding.calcButton.setOnClickListener {
            val boardSize =
                binding.boardSizeEdit.text.toString().let { if (it.isEmpty()) 0 else it.toInt() }
            val queens =
                binding.queensAmountEdit.text.toString().let { if (it.isEmpty()) 0 else it.toInt() }
            val kings =
                binding.kingSizeEdit.text.toString().let { if (it.isEmpty()) 0 else it.toInt() }
            viewModel.startCalc()
            viewModel.calc(boardSize, queens, kings)
        }

        /*binding.queensAndKingsCount.visibility = View.GONE
        binding.queensCount.visibility = View.GONE*/

        val viewModel by viewModels<CalcViewModel>()
        this.viewModel = viewModel
        viewModel.calcState.observe(
            viewLifecycleOwner, getCalcStateObserver()
        )

        return binding.root
    }

    private fun switchBtns(calcBtn: Int, cancelBtn: Int) {
        binding.cancelButton.background =
            Util.getDrawable(resources, cancelBtn)
        binding.calcButton.background =
            Util.getDrawable(resources, calcBtn)
        if (cancelBtn == R.drawable.inactive_button_back) {
            binding.cancelButton.isClickable = false
            binding.calcButton.isClickable = true
        } else {
            binding.cancelButton.isClickable = true
            binding.calcButton.isClickable = false
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Toast.makeText(
            requireContext(),
            resources.getString(R.string.low_mem_msg),
            Toast.LENGTH_LONG
        )
            .show()
    }

    private fun getCalcStateObserver(): Observer<CalcState> {
        return Observer { calcState ->
            when (calcState) {
                CalcState.DONE -> {
                    binding.queensCountVar = viewModel.queensCount?.toString()
                    binding.queensAndKingsCountVar = viewModel.queensAndKingsCount?.toString()
                    binding.executePendingBindings()
                    switchBtns(
                        calcBtn = R.drawable.button_back,
                        cancelBtn = R.drawable.inactive_button_back
                    )
                    //remove prog bar
                }

                CalcState.IN_PROGRESS -> {
                    switchBtns(
                        calcBtn = R.drawable.inactive_button_back,
                        cancelBtn = R.drawable.cancel_button_back
                    )
                    //set prog bar
                }
                CalcState.CANCEL -> {
                    viewModel.cancelCalcJob()
                    switchBtns(
                        calcBtn = R.drawable.button_back,
                        cancelBtn = R.drawable.inactive_button_back
                    )
                    //remove prog bar
                }
                else -> {

                }
            }
        }
    }
}