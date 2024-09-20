import * as React from "react";
import { cn } from "@/lib/utils.ts";
import useFormField from "../../../hooks/useFormField";

type FormMessageProps = React.HTMLAttributes<HTMLParagraphElement>;

const FormMessage = React.forwardRef<
    HTMLParagraphElement,
    FormMessageProps
>(({ className, children, ...props }, ref) => {
    const { error, formMessageId } = useFormField();
    const body = error ? String(error.message) : children;

    if (!body) {
        return null;
    }

    return (
        <p
            ref={ref}
            id={formMessageId}
            className={cn("text-[0.8rem] font-medium text-destructive", className)}
            {...props}
        >
            {body}
        </p>
    );
});
FormMessage.displayName = "FormMessage";

export default FormMessage;