export interface Item {
  name: string;
  quantity: number;
  price: number;
}

export interface ValidationError {
  field: string;
  error: string;
}

export interface ErrorResponse {
  message: string;
  errors?: ValidationError[];
}
