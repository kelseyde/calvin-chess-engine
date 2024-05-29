# What is an Activation Function?

A function that decides how activated a neuron is. It takes the all the neuron's inputs, weights and bias, and
transforms them into an output representing the activation level of the neuron.

### Why do neurons need an activation function?

The purpose of an activation function is to add *non-linearity* to the neural network.

Suppose we have a neural network working without activation functions. Every neuron will only be performing a linear
transformation on the inputs using the weights and biases. It doesn’t matter how many layers we build in to the network;
all layers behave in the same way because the composition of two linear functions is a linear function itself.

Although the neural network becomes simpler, learning any complex task is impossible,

### Types of activation function

### 1. Binary Step

If the input is above a certain threshold, the neuron is fully activated. Otherwise, it is fully de-activated.

This function is very simple but has fundamental limitations:

1. It cannot provide multi-value outputs—for example, it cannot be used for multi-class classification problems.
2. The gradient of the step function is zero, which causes a hindrance in the backpropagation process.

### 2. Linear

This is essentially an identity function: the output is directly proportional to the input.

The function doesn't do anything to the weighted sum of the input; it simply spits out the value it was given.

However, a linear activation function has two major problems :

1. It’s not possible to use backpropagation as the function's derivative is a constant and has no relation to the input x.
2. All layers of the neural network will collapse into one if a linear activation function is used.

In order for all the layers, weights and biases to actually *mean* anything, such that adjusting them will affect the 
power of the network as a whole, we need to introduce non-linearity into the network.

### 3. Sigmoid

This function takes any real value as input and outputs values in the range of 0 to 1.

The larger the input (more positive), the closer the output value will be to 1.0, whereas the smaller the input (more 
negative), the closer the output will be to 0.0.

Advantages of sigmoid:

1. It is effective for models where we have to predict the probability as an output (since probability is represented as
ranging from 0.0 to 1.0).
2. The function is differentiable and provides a smooth gradient, i.e., preventing jumps in output values. This is 
represented by an S-shape of the sigmoid activation function. 

Disadvantages:

1.The *Vanishing Gradient* problem: the gradient of a sigmoid smooths out dramatically with very positive/negative inputs. 
As the gradient approaches zero, the network ceases to learn, making the training of the network more difficult and unstable.

### 4. ReLU

Stands for Rectified Linear Unit.

If the input is greater than zero, the output is identical to the input. Otherwise, the input is zero.

In Java it can be represented as:

```
float output = Math.max(0, input);
```

Advantages:

1. A very simple calculation, so it is computationally efficient. 
2. ReLU accelerates the convergence of gradient descent towards the global minimum of the loss function due to its linear, 
non-saturating property.

Disadvantages:

1. The *Dying ReLU Problem*: (look this up later)

### 5. ClippedReLU

ClippedReLU is a variant of the ReLU activation function that adds an upper bound to the output. In Java:

```
float output = Math.min(Math.max(input, 0), threshold);
```

Advantages:

1. Useful in scenarios where it is beneficial to limit the range of activation values to prevent extremely high activations 
which might cause instability in the network.
2. Non-Linearity: Still introduces non-linearity like ReLU.

Disadvantages:

1. Less Common: Not as widely used as ReLU, and the choice of threshold can require tuning.
2. Potential Information Loss: Clipping can lead to loss of information if the threshold is set too low.