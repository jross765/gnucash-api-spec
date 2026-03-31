# Notes on the Module "API Specialized Entities"

## What Does It Do?

This module provides specialized entities (classes) that are based on more generic entities in the module "API (Core)".

## What is This Repo's Relationship with the Other Repos?

* This is a module-level repository which is part of a multi-module project, i.e. it has a parent and several siblings. 

  [Parent](https://github.com/jross765/JGnuCashLibNTools.git)

* Under normal circumstances, you cannot compile it on its own (at least not without further preparation), but instead, you should clone it together with the other repos and use the parent repo's build-script.

## Major Changes
### V. 0.2 &rarr; 0.3
**Introduced:**

(Nothing)

**Improvements:**

* `GnuCash(Writable)StockBuyTransaction(Impl)`: Improvements (changed interface):

    Added various methods to get and set data; now even better aligned to business perspective.

* `GnuCash(Writable)StockDividendTransaction(Impl)`: dto.

* Fixed bugs

* Better test coverage

### V. 0.1 &rarr; 0.2
**Introduced:**

* `GnuCash(Writable)StockDividendTransaction(Impl)`

**Improvements:**

* Added to the real added value of this module's classes: Added non-trivial special methods
  and removed other ones (changed interface), now better aligned to business perspective.

* Analogously, for the writable variants of theses classes, introduced various methods
  to set data and removed other ones.

* For all the above-mentioned new methods: The `BigFraction` variant, as well.

* For all `GnuCashWritableXYZTransaction` classes: Proper test cases / data.
  (Now they are all covered)

* Overall: 
    * Fixed bugs
    * A few minor improvements here and there.

### V. 0.1
New.

**Introduced:**

* `GnuCashSimpleTransaction` (includes code that used to be in module "API" and which does not belong there)

* `GnuCashStockBuyTransaction`

* `GnuCashStockSplitTransaction`

## Planned

* Other kinds of special transactions, e.g.:

  * Single transactions:
    * Stock/security sell transaction
    * Foreign currency transaction
    * Impairment transaction
    * Salary payment transaction
    * Book-closing transaction (EOY)

  * Multi-transactions:
    * Move-stocks/securities transaction (from one securities account to another)
    * Loan-related transactions
    * Crypto-currency transaction (buy s.t. w/ crypto = spec. security)

* Support building up the specialized entities
(at least the more complex ones, such as stock-buy/dividend transaction).

  Currently, only existing ones are supported that you then can amend.

## Known Issues
(None)

## Notes on Scope
* This module only contains data object classes. The classes that *generate*/*handle* them are located in the module "API Extensions".

* When you look at this module and at the module 
"[API](https://github.com/jross765/gnucash-api.git)", 
you might wonder: "There are a couple of special classes in the module 'API', their resp. packages are even called `spec` -- shouldn't they be located here, in the module dedicated to specialized entities"?

  **Short answer**

  The special classes in the module "API" are not the kind of "special" that we mean here, in this module.

  **Longer answer**

  The author / current maintainer has had more than one thought over this. Here is his current assessment (march 2026):

  He has -- as an experiment -- tried to move the `spec` packages from module "API (Core)" to this one. Without getting too much into the technical details: It is possible, of course, but it would necessitate a major redesign, to the point of almost being a project of its own. And even if he did that: The result would not be very satisfactory, neither from his own point of view (maintenance) nor from the point of view of the user-developer. You would have a whole bunch of confusing "base" variants of classes in "API (Core)" and extended ones (derived from the base classes) in this module -- so many that it would be confusing; it would, in many instances, not be clear which one to choose without getting *very* deep into the weeds.

  As for the "kind of 'special'": 

  * The main reason why the maintainer introduced the `spec` packages in module "API" is the fact that GnuCash has the generalized entities "(generic) invoice" and "(generic) job" to represent the various variants of them (on business logic level) internally. The "special" classes represented by them are, in fact, "regular" business objects (a customer invoice, e.g., is not the same as a vendor bill, speaking in business terms, even if both are represented by the same kind of object internally). Thus, in module "API", the "specialness" is a matter of the technical data model, the "generic" classes usually only being used internally. 

  * This module, in contrast, handles special variants of "genuine" business classes that can have special variants on the *business logic* level (regardless of how they are represented internally).
