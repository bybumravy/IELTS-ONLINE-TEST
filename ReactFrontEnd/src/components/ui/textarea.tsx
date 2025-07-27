import React from 'react';

type TextareaProps = React.TextareaHTMLAttributes<HTMLTextAreaElement>;

const Textarea = React.forwardRef<HTMLTextAreaElement, TextareaProps>(({ className = '', ...props }, ref) => {
    return (
        <textarea
            ref={ref}
            {...props}
            className={`border border-gray-300 rounded px-3 py-2 w-full resize-none focus:outline-none focus:ring-2 focus:ring-green-500 ${className}`}
        />
    );
});

Textarea.displayName = 'Textarea';

export { Textarea };