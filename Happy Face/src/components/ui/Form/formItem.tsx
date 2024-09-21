import * as React from "react";
import { cn } from "@/lib/utils.ts";
import { FormItemContext } from "@/contexts/FormItemContext.ts";

type FormItemProps = React.HTMLAttributes<HTMLDivElement>;

const FormItem = React.forwardRef<HTMLDivElement, FormItemProps>(({ className, ...props }, ref) => {
    const id = React.useId();

    return (
        <FormItemContext.Provider value={{ id }}>
            <div ref={ref} className={cn("space-y-2", className)} {...props} />
        </FormItemContext.Provider>
    );
});
FormItem.displayName = "FormItem";

export default FormItem;