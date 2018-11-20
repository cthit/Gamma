import React from "react";
import * as yup from "yup";
import GammaEditData from "../../../../../common/elements/gamma-edit-data";
import translations from "./WebsiteForm.view.translations.json";
import {
    DigitTranslations,
    DigitTextField
} from "@cthit/react-digit-components";

const WebsiteForm = ({ initialValues, onSubmit, titleText, submitText }) => (
    <DigitTranslations
        translations={translations}
        uniquePath="Websites.Screen.CommonView.WebsiteForm"
        render={text => (
            <GammaEditData
                initialValues={initialValues}
                onSubmit={onSubmit}
                validationSchema={yup.object().shape({
                    name: yup.string().required(text.FieldRequired),
                    prettyName: yup.string().required(text.FieldRequried)
                })}
                titleText={titleText}
                submitText={submitText}
                keysOrder={["name", "prettyName"]}
                keysComponentData={{
                    name: {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.Name
                        }
                    },
                    prettyName: {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.PrettyName
                        }
                    }
                }}
            />
        )}
    />
);

export default WebsiteForm;
