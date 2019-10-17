import {
    DigitButton,
    DigitForm,
    DigitFormField,
    DigitTranslations,
    DigitDesign,
    DigitLayout,
    DigitTextField
} from "@cthit/react-digit-components";
import PropTypes from "prop-types";
import React from "react";
import * as yup from "yup";
import translations from "./InputCid.view.translations";

class InputCid extends React.Component {
    componentDidMount() {
        this.props.deltaLoadingFinished();
    }

    render() {
        const { sendCid, redirectTo, toastOpen } = this.props;

        return (
            <DigitTranslations
                translations={translations}
                render={text => (
                    <DigitLayout.MarginTop>
                        <DigitLayout.Center>
                            <DigitForm
                                validationSchema={yup.object().shape({
                                    cid: yup
                                        .string()
                                        .required(text.FieldRequired)
                                })}
                                initialValues={{ cid: "" }}
                                onSubmit={(values, actions) => {
                                    sendCid(values)
                                        .then(response => {
                                            actions.resetForm();
                                            actions.setSubmitting(false);
                                            redirectTo(
                                                "/create-account/email-sent"
                                            );
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
                                            <DigitLayout.Center>
                                                <DigitFormField
                                                    name="cid"
                                                    component={DigitTextField}
                                                    componentProps={{
                                                        upperLabel: text.Cid,
                                                        outlined: true,
                                                        maxLength: 12
                                                    }}
                                                />
                                            </DigitLayout.Center>
                                        </DigitDesign.CardBody>
                                        <DigitDesign.CardButtons
                                            reverseDirection
                                        >
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
                        </DigitLayout.Center>
                    </DigitLayout.MarginTop>
                )}
            />
        );
    }
}

InputCid.propTypes = {
    sendCid: PropTypes.func.isRequired
};

export default InputCid;
