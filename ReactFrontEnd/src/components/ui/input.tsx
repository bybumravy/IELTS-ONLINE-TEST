import React from 'react';

type InputProps = React.InputHTMLAttributes<HTMLInputElement>;

const Input = React.forwardRef<HTMLInputElement, InputProps>(({ className = '', ...props }, ref) => {
    return (
        <input
            ref={ref}
            {...props}
            className={`border border-gray-300 rounded px-3 py-2 w-full focus:outline-none focus:ring-2 focus:ring-green-500 ${className}`}
        />
    );
});

Input.displayName = 'Input';

export { Input };