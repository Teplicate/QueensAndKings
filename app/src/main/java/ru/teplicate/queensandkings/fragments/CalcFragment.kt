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

        binding.cancelButton.setOnClickListener {
            viewModel.cancelCalc()
        }

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
        Util.showToast(R.string.low_mem_msg, requireContext())
    }

    private fun getCalcStateObserver(): Observer<CalcState> {
        return Observer { calcState ->
            when (calcState) {
                CalcState.DONE -> {
                    Util.showToast(R.string.done_calc_msg, requireContext())
                    binding.queensCountVar = viewModel.queensCount?.toString()
                    binding.queensAndKingsCountVar = viewModel.queensAndKingsCount?.toString()
                    binding.executePendingBindings()
                    switchBtns(
                        calcBtn = R.drawable.button_back,
                        cancelBtn = R.drawable.inactive_button_back
                    )
                }

                CalcState.IN_PROGRESS -> {
                    Util.showToast(R.string.starting_calc_msg, requireContext())
                    switchBtns(
                        calcBtn = R.drawable.inactive_button_back,
                        cancelBtn = R.drawable.cancel_button_back
                    )
                    binding.queensAndKingsCountVar = resources.getString(R.string.calc_in_prog_text)
                    binding.queensCountVar = resources.getString(R.string.calc_in_prog_text)
                    binding.executePendingBindings()
                }
                CalcState.CANCEL -> {
                    Util.showToast(R.string.cancelling_calc_msg, requireContext())
                    viewModel.cancelCalcJob()
                    switchBtns(
                        calcBtn = R.drawable.button_back,
                        cancelBtn = R.drawable.inactive_button_back
                    )
                }
                CalcState.CANCELLED -> {
                    Util.showToast(R.string.cancelled_calc_msg, requireContext())
                    binding.queensCountVar = resources.getString(R.string.cancelled_str)
                    binding.queensAndKingsCountVar = resources.getString(R.string.cancelled_str)
                    binding.executePendingBindings()
                }
                else -> {

                }
            }
        }
    }
}