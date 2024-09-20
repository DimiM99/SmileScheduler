import * as React from "react";
import * as LabelPrimitive from "@radix-ui/react-label";
import { cn } from "@/lib/utils.ts";
import { Label } from "@/components/ui/label.tsx";
import useFormField from "@/hooks/useFormField.ts";

type FormLabelProps = React.ComponentPropsWithoutRef<typeof LabelPrimitive.Root>;

const FormLabel = React.forwardRef<
    React.ElementRef<typeof LabelPrimitive.Root>,
    FormLabelProps
>(({ className, ...props }, ref) => {
    const { formItemId } = useFormField();

    return (
        <Label
            ref={ref}
            className={cn(className)}
            htmlFor={formItemId}
            {...props}
        />
    );
});
FormLabel.displayName = "FormLabel";

export default FormLabel;