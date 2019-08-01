import React from "react";
import {
    DigitIfElseRendering,
    DigitTranslations,
    DigitLayout,
    DigitForm,
    DigitDesign,
    DigitFormField,
    DigitButton,
    DigitTextField
} from "@cthit/react-digit-components";
import * as yup from "yup";
import translations from "./DeleteMe.view.translations";
import Delete from "@material-ui/icons/Delete";
import statusCode from "../../../../../../common/utils/formatters/statusCode.formatter";
import statusMessage from "../../../../../../common/utils/formatters/statusMessage.formatter";

class DeleteMe extends React.Component {
    componentDidMount() {
        const { gammaLoadingFinished } = this.props;
        gammaLoadingFinished();
    }

    render() {
        const { me, deleteMe, toastOpen, redirectTo } = this.props;
        return (
            <DigitIfElseRendering
                test={me != null}
                ifRender={() => (
                    <DigitTranslations
                        translations={translations}
                        render={text => (
                            <DigitLayout.MarginTop>
                                <DigitLayout.Center>
                                    <DigitForm
                                        validationSchema={yup.object().shape({
                                            password: yup
                                                .string()
                                                .required(text.FieldRequired)
                                                .min(8, text.MinimumLength)
                                        })}
                                        initialValues={{ password: "" }}
                                        onSubmit={(values, actions) => {
                                            deleteMe(values)
                                                .then(() => {
                                                    toastOpen({
                                                        text:text.MeDeleted
                                                    });
                                                    redirectTo("/login");
                                                })
                                                .catch(error => {
                                                    const code = statusCode(
                                                        error
                                                    );
                                                    const message = statusMessage(
                                                        error
                                                    );
                                                    let errorMessage =
                                                        text.SomethingWentWrong;
                                                    if (code === 422) {
                                                        if (
                                                            message ===
                                                            "INCORRECT_CID_OR_PASSWORD"
                                                        ) {
                                                            errorMessage =
                                                                text.PasswordDoesNotMatch;
                                                        }
                                                    }
                                                    toastOpen({
                                                        text: errorMessage
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
                                                    text={text.DeleteMe}
                                                />
                                                <DigitDesign.CardSubTitle
                                                    text={
                                                        text.DeleteMeDescription
                                                    }
                                                />
                                                <DigitDesign.CardBody>
                                                    <DigitLayout.Center>
                                                        <DigitFormField
                                                            name="password"
                                                            component={
                                                                DigitTextField
                                                            }
                                                            componentProps={{
                                                                upperLabel:
                                                                    text.Password,
                                                                filled: false,
                                                                password: true
                                                            }}
                                                        />
                                                    </DigitLayout.Center>
                                                </DigitDesign.CardBody>
                                                <DigitDesign.CardButtons
                                                    reverseDirection
                                                >
                                                    <DigitButton
                                                        text={text.Delete}
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
                )}
            />
        );
    }
}

export default DeleteMe;
