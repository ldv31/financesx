#Description of the various computed statistics

##Global Statistics
###Description: 

* Input data: all operations
* Sums the values of all operation of a category
* Provide the sum for each category
* Applicable to expenses and income categories

###Computation method:

* Loop on all operations and add the operation value to the appropriate category

&nbsp;
##History Statistics
###Description: 

* Input data: all operations
* Sums the values of all operation of a category and per month
* Provide the sum for each month of each category
* Applicable to expenses and income categories

###Computation method:

* Create per category a structure with all months from first operation date to last operation date 
* Loop on all operations and add the operation value to the appropriate category and month

&nbsp;
##Gain and Loss Statistics
###Description: 

* Input data: all operations except for category "Epargne" (investment in EARL is still considered as the money is no longer available)
* Sums the values of all incomes and expenses of a month
* Provide the sum for each month
* Provide also a cummulative view 

###Computation method:

* Create per  a structure with all months from first operation date to last operation date 
* Loop on all operations and add the operation expense or income to the appropriate month
* Exclude operation from category "Epargne"