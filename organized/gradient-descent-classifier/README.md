# Gradient Descent for Linear/Logistic Regression

A machine learning classifier built from scratch — no ML libraries — split into two programs:

- **`GradientDescent.java`** — reads a CSV dataset and trains a regression model via
  gradient descent, with three interchangeable activation functions (linear, logistic/sigmoid,
  arctan), each with its own hand-derived gradient.
- **`Predict.java`** — takes a trained model's coefficients and reports the classifier's
  error rate on a dataset (also supports ReLU).

## Compiling and running

```bash
javac GradientDescent.java Predict.java
java GradientDescent -file data.csv -logistic
java Predict -file data.csv -logistic 0.5 1.2 -0.3
```

Both programs read CSV from a file (`-file <name>`) or stdin if no file is given. Run
with no arguments to see the full list of flags (`-yes`, `-no`, `-headers`, `-labels`,
`-logistic`, `-arctan`, `-relu`, `-verbose`).
