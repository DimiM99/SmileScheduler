import * as React from "react";
import { cn } from "@/lib/utils.ts";
import useFormField from "../../../hooks/useFormField";

type FormDescriptionProps = React.HTMLAttributes<HTMLParagraphElement>;

const FormDescription = React.forwardRef<
    HTMLParagraphElement,
    FormDescriptionProps
>(({ className, ...props }, ref) => {
    const { formDescriptionId } = useFormField();

    return (
        <p
            ref={ref}
            id={formDescriptionId}
            className={cn("text-[0.8rem] text-muted-foreground", className)}
            {...props}
        />
    );
});
FormDescription.displayName = "FormDescription";

export default FormDescription;