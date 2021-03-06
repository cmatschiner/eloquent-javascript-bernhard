/* Class Matrix, acting as a two-dimensional array
The class stores its content in a single array of 
width × height elements. The elements are stored row by 
row, so, for example, the third element in the fifth row 
is (using zero-based indexing) stored at position 
4 × width + 2. 

Two-dimensional array w(idth) / h(eight):
#  0       1       2       w
0  e(0,0)  e(0,1)  e(0,2)  e(0,x)
1  e(1,0)  e(1,1)  e(1,2)
h  e(y,0)                  e(x,y) */

class Matrix {

    constructor(width, height, element = (x, y) => undefined) {
        /* The constructor function takes a width, a height, 
        and an optional content function that will be used 
        to fill in the initial values. */
        this.width = width;
        this.height = height;
        this.content = [];

        // loops to "fill" array
        for (let y = 0; y < height; y++) {
            for (let x = 0; x < width; x++) {
                // interface
                this.content[y * width + x] = element(x, y);

            }
        }
    }

    // shorthand method to retrieve element from the matrix
    get(x, y) {

        return this.content[y * this.width + x];
    }

    // shorthand method to update element in the matrix
    set(x, y, value) {
        this.content[y * this.width + x] = value;
    }
}


// Class MatrixIterator
// Produces objects with x, y, and value properties
class MatrixIterator {
    constructor(matrix) {
        this.x = 0;
        this.y = 0;
        this.matrix = matrix;
    }

    next() {
        if (this.y == this.matrix.height) return {
            done: true
        };

        let value = {
            x: this.x,
            y: this.y,
            value: this.matrix.get(this.x, this.y)
        };
        this.x++;
        if (this.x == this.matrix.width) {
            this.x = 0;
            this.y++;
        }
        return {
            value,
            done: false
        };
    }
}

console.log(Matrix.prototype); // -> Matrix {}

/* User defined iterable: after-the-fact prototype manipulation to add below method to the prototype of the class Matrix */
Matrix.prototype[Symbol.iterator] = function () {
    return new MatrixIterator(this);
};

console.log(Matrix.prototype);
// -> Matrix {[Iterator] [Symbol(Symbil.iterator)] : [Function]}

let matrix = new Matrix(2, 2, (x, y) => `value ${x},${y}`);
/* The new operator creates an instance of a user-defined object type 
or of one of the built-in object types that has a constructor function */
for (let {
        x,
        y,
        value
    } of matrix) {
    console.log(x, y, value);
    // → 0 0 'value 0,0', 1 0 'value 1,0', 0 1 'value 0,1'
}

