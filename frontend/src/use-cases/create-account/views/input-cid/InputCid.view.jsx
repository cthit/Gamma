import {
    DigitButton,
    DigitForm,
    DigitFormField,
    DigitTranslations,
    DigitDesign
} from "@cthit/react-digit-components";
import PropTypes from "prop-types";
import React from "react";
import * as yup from "yup";
import { Center, MarginTop } from "../../../../common-ui/layout";
import { CIDInput } from "./InputCid.view.styles";
import translations from "./InputCid.view.translations";

const InputCid = ({ sendCid, redirectTo, toastOpen }) => (
    <DigitTranslations
        translations={translations}
        uniquePath="CreateAccount"
        render={text => (
            <MarginTop>
                <Center>
                    <DigitForm
                        validationSchema={yup.object().shape({
                            cid: yup.string().required(text.FieldRequired)
                        })}
                        initialValues={{ cid: "" }}
                        onSubmit={(values, actions) => {
                            sendCid(values)
                                .then(response => {
                                    actions.resetForm();
                                    redirectTo("/create-account/email-sent");
                                })
                                .catch(error => {
                                    toastOpen({
                                        text: text.SomethingWentWrong,
                                        duration: 10000
                                    });
                                });
                        }}
                        render={({ errors, touched }) => (
                            <DigitDesign.Card
                                absWidth="300px"
                                absHeight="300px"
                                hasSubTitle
                            >
                                <DigitDesign.CardTitle
                                    text={text.EnterYourCid}
                                />
                                <DigitDesign.CardSubTitle
                                    text={text.EnterYourCidDescription}
                                />
                                <DigitDesign.CardBody>
                                    <Center>
                                        <DigitFormField
                                            name="cid"
                                            component={CIDInput}
                                            componentProps={{
                                                upperLabel: text.Cid
                                            }}
                                        />
                                    </Center>
                                </DigitDesign.CardBody>
                                <DigitDesign.CardButtons reverseDirection>
                                    <DigitButton
                                        text={text.SendCid}
                                        primary
                                        raised
                                        submit
                                    />
                                </DigitDesign.CardButtons>
                            </DigitDesign.Card>
                        )}
                    />
                </Center>
            </MarginTop>
        )}
    />
);

InputCid.propTypes = {
    sendCid: PropTypes.func.isRequired
};

export default InputCid;
