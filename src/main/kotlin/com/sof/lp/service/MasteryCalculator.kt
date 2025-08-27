package com.sof.lp.service

import com.sof.lp.entity.StudentQuestionHistory
import org.apache.commons.math3.optim.MaxEval
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType
import org.apache.commons.math3.optim.univariate.BrentOptimizer
import org.apache.commons.math3.optim.univariate.SearchInterval
import org.apache.commons.math3.optim.univariate.UnivariateObjectiveFunction
import org.springframework.stereotype.Service
import kotlin.math.exp
import kotlin.math.ln

@Service
class MasteryCalculator {

    fun huntTheta(responses: List<StudentQuestionHistory>): Double {
        if (responses.isEmpty()) return 0.0  // Neutral default

        // Define the function to minimize: Negative log-likelihood (univariate wrapper)
        val negLogL = UnivariateObjectiveFunction { theta ->
            var logL = 0.0
            responses.forEach { resp ->
                val d = when (resp.difficulty.lowercase()) {
                    "easy" -> -1.0
                    "difficult" -> 1.0
                    else -> 0.0  // Neutral fallback; log warning in service
                }
                val p = 1.0 / (1.0 + exp(-(theta - d)))
                logL += if (resp.correct) ln(p.coerceIn(1e-10, 1 - 1e-10)) else ln((1 - p).coerceIn(1e-10, 1 - 1e-10))
            }
            -logL  // Negative for minimization
        }

        // Create BrentOptimizer with relative and absolute tolerances
        val optimizer = BrentOptimizer(1e-10, 1e-14)  // rel, abs (adjust as needed for precision)

        // Optimize with bounds (-10 to 10), max evaluations (e.g., 100), initial guess optional via interval
        val result = optimizer.optimize(
            MaxEval(100),  // Max function evaluations to prevent long runs
            negLogL,  // The objective function
            GoalType.MINIMIZE,
            SearchInterval(-10.0, 10.0)  // Bounds for θ
        )

        return result.point  // The optimized θ
    }
}