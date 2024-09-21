import * as React from "react";
import { Slot } from "@radix-ui/react-slot";
import useFormField from "@/hooks/useFormField.ts";

type FormControlProps = React.ComponentPropsWithoutRef<typeof Slot>;

const FormControl = React.forwardRef<
    React.ElementRef<typeof Slot>,
    FormControlProps
>(({ ...props }, ref) => {
    const { error, formItemId, formDescriptionId, formMessageId } = useFormField();

    return (
        <Slot
            ref={ref}
            id={formItemId}
            aria-describedby={
                error
                    ? `${formDescriptionId} ${formMessageId}`
                    : formDescriptionId
            }
            aria-invalid={error ? true : undefined}
            {...props}
        />
    );
});
FormControl.displayName = "FormControl";

export default FormControl;