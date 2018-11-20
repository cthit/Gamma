import {
    DigitTextField,
    DigitTranslations
} from "@cthit/react-digit-components";
import React from "react";
import * as yup from "yup";
import { Center } from "../../../../common-ui/layout";
import IfElseRendering from "../../../../common/declaratives/if-else-rendering";
import translations from "./EditActivationCodeDetails.screen.translations.json";

import { DigitEditData } from "@cthit/react-digit-components";

const EditActivationCodeDetails = ({
    activationCode,
    activationCodesChange
}) => (
    <IfElseRendering
        test={activationCode != null}
        ifRender={text => (
            <DigitTranslations
                translations={translations}
                uniquePath="ActivationCodes.Screen.EditActivationCodeDetails"
                render={text => (
                    <Center>
                        <DigitEditData
                            initialValues={activationCode}
                            onSubmit={(values, actions) => {
                                console.log(values);
                            }}
                            validationSchema={yup.object().shape({
                                code: yup.string().required(text.FieldRequired)
                            })}
                            titleText={text.EditActivationCode}
                            submitText={text.SaveActivationCode}
                            keysOrder={["code"]}
                            keysComponentData={{
                                code: {
                                    component: DigitTextField,
                                    componentProps: {
                                        upperLabel: text.Code
                                    }
                                }
                            }}
                        />
                    </Center>
                )}
            />
        )}
    />
);

export default EditActivationCodeDetails;
