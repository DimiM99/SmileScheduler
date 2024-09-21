import { Controller, ControllerProps, FieldPath, FieldValues } from "react-hook-form";
import { FormFieldContext } from "@/contexts/FormFieldContext.ts";

type FormFieldProps<
    TFieldValues extends FieldValues = FieldValues,
    TName extends FieldPath<TFieldValues> = FieldPath<TFieldValues>
> = Omit<ControllerProps<TFieldValues, TName>, 'rules'> & {
    rules?: Omit<ControllerProps<TFieldValues, TName>['rules'], 'validate'> & {
        validate?: (value: never) => boolean | string | Promise<boolean | string>
    }
};

const FormField = <
    TFieldValues extends FieldValues = FieldValues,
    TName extends FieldPath<TFieldValues> = FieldPath<TFieldValues>
>({
      ...props
  }: FormFieldProps<TFieldValues, TName>) => {
    return (
        <FormFieldContext.Provider value={{ name: props.name }}>
            <Controller {...props} />
        </FormFieldContext.Provider>
    )
}

export default FormField;