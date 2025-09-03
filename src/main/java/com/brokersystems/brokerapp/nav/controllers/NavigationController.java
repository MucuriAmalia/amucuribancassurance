package com.brokersystems.brokerapp.nav.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
/**
 * System Main Navigation Controller
 * @author mugenyq
 *
 */
@Controller
@RequestMapping(value = "/protected/home")
public class NavigationController {
	
	 @RequestMapping(value="/orgsetups",method = RequestMethod.GET)
	    public ModelAndView orgSetups() {
				return new ModelAndView("orgSetupScreen");
	    }
	 
	 /**
	  * Navigation to System Setups Screen
	  * @return
	  */
	 @RequestMapping(value="/setups",method = RequestMethod.GET)
	    public ModelAndView tensetups() {
	        return new ModelAndView("uwsetupScreen");
	    }

	/**
	 * Navigation to Accounts Setups Screen
	 * @return
	 */
	@RequestMapping(value="/accountsetups",method = RequestMethod.GET)
	public ModelAndView accountsetups() {
		return new ModelAndView("accountsetups");
	}

	/**
	 * Navigation to Medical Setups Screen
	 * @return
	 */
	@RequestMapping(value="/medicalsetups",method = RequestMethod.GET)
	public ModelAndView medsetups() {
		return new ModelAndView("medsetups");
	}

	/**
	 * Navigation to Finance Setups Screen
	 * @return
	 */
	@RequestMapping(value="/financesetups",method = RequestMethod.GET)
	public ModelAndView financesetups() {
		return new ModelAndView("financesetups");
	}

	/**
	 * Navigation to Bulk policy upload Screen
	 * @return
	 */
	@RequestMapping(value="/bulkuploadscreen",method = RequestMethod.GET)
	public ModelAndView bulkuploadscreen() {
		return new ModelAndView("bulkuploadscreen");
	}

	/**
	 * Navigation to Bulk receipt upload Screen
	 * @return
	 */
	@RequestMapping(value="/bulkrecuploadscreen",method = RequestMethod.GET)
	public ModelAndView bulkrecuploadscreen() {
		return new ModelAndView("bulkrecuploadscreen");
	}
	/**
	 * Navigation to approved bulk report Screen
	 * @return
	 */
	@RequestMapping(value="/approvedbulkreport",method = RequestMethod.GET)
	public ModelAndView approvedbulkreport() {
		return new ModelAndView("approvedbulkreport");
	}

	/**
	 * Navigation to Data Migration Screen
	 * @return
	 */
	@RequestMapping(value="/datamigrationscreen",method = RequestMethod.GET)
	public ModelAndView datamigrationscreen() {
		return new ModelAndView("datamigrationscreen");
	}

	/**
	 * Navigation to policy update Screen
	 * @return
	 */
	@RequestMapping(value="/updatepolnoscreen",method = RequestMethod.GET)
	public ModelAndView polnoUpdatescreen() {
		return new ModelAndView("updatepolnoscreen");
	}

	/**
	  * Navigation to System Transaction Screen
	  * @return
	  */
	 @RequestMapping(value="/trans",method = RequestMethod.GET)
	    public ModelAndView trans() {
	        return new ModelAndView("tentrans");
	    }

}
