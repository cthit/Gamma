import * as yup from "yup";
import { DigitTextField } from "@cthit/react-digit-components";

export const validationSchema = text => {
    const schema = {};

    return yup.object().shape(schema);
};

export const initialValues = () => {
    const output = {};

    output.type = "";

    return output;
};

export const keysComponentData = text => {
    const componentData = {};

    componentData.type = {
        component: DigitTextField,
        componentProps: {
            outlined: true,
            maxLength: 30
        }
    };

    return componentData;
};

export const keysText = text => {
    const keysText = {};

    keysText.type = text.SuperGroupType;

    return keysText;
};

export const keysOrder = ["type"];
