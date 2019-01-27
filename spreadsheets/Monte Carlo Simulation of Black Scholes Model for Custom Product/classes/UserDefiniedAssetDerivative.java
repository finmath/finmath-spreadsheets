
import net.finmath.exception.CalculationException;
import net.finmath.montecarlo.assetderivativevaluation.products.AbstractAssetMonteCarloProduct;
import net.finmath.montecarlo.assetderivativevaluation.AssetModelMonteCarloSimulationModel;
import net.finmath.stochastic.RandomVariable;

/**
 * Implements pricing of a European stock option.
 * 
 * @author Christian Fries
 * @version 1.2
 */
public class UserDefiniedAssetDerivative extends AbstractAssetMonteCarloProduct {

	private double maturity;
	private double strike;
	
	/**
	 * @param strike
	 * @param maturity
	 */
	public UserDefiniedAssetDerivative(double maturity, double strike) {
		super();
		this.maturity	= maturity;
		this.strike		= strike;
	}

	/**
	 * This method returns the value random variable of the product within the specified model, evaluated at a given evalutationTime.
	 * Note: For a lattice this is often the value conditional to evalutationTime, for a Monte-Carlo simulation this is the (sum of) value discounted to evaluation time.
	 * Cashflows prior evaluationTime are not considered.
	 * 
	 * @param evaluationTime The time on which this products value should be observed.
	 * @param model The model used to value the product.
	 * @return The random variable representing the value of the product discounted to evaluation time
	 */
	@Override
	public RandomVariable getValue(double evaluationTime, AssetModelMonteCarloSimulationModel model) throws CalculationException {
		// Get underlying and numeraire
		RandomVariable underlyingAtMaturity	= model.getAssetValue(maturity,0);
		
		// The payoff
		RandomVariable values = underlyingAtMaturity.sub(strike).floor(0.0);
		
		// Discounting...
		RandomVariable	numeraireAtMaturity		= model.getNumeraire(maturity);
		values = values.div(numeraireAtMaturity);

		// ...to evaluation time.
		RandomVariable	numeraireAtZero				= model.getNumeraire(evaluationTime);
		values= values.mult(numeraireAtZero);

		return values;
	}
}
