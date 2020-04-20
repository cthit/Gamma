import {
    DigitTextField,
    useDigitTranslations,
    DigitEditDataCard,
    DigitLayout,
    useDigitToast
} from "@cthit/react-digit-components";
import React from "react";
import * as yup from "yup";
import translations from "./InputCid.view.translations";
import { useHistory } from "react-router-dom";
import { activateCid } from "../../../../api/whitelist/post.whitelist.api";

const InputCid = () => {
    const [queueToast] = useDigitToast();
    const [text] = useDigitTranslations(translations);
    const history = useHistory();

    return (
        <DigitLayout.Center>
            <DigitEditDataCard
                centerFields
                validationSchema={yup.object().shape({
                    cid: yup.string().required(text.FieldRequired)
                })}
                initialValues={{ cid: "" }}
                onSubmit={(values, actions) => {
                    activateCid(values)
                        .then(response => {
                            actions.resetForm();
                            actions.setSubmitting(false);
                            history.push("/create-account/email-sent");
                        })
                        .catch(error => {
                            queueToast({
                                text: text.SomethingWentWrong,
                                duration: 10000
                            });
                        });
                }}
                keysComponentData={{
                    cid: {
                        component: DigitTextField,
                        componentProps: {
                            upperLabel: text.Cid,
                            outlined: true,
                            maxLength: 12
                        }
                    }
                }}
                keysOrder={["cid"]}
                size={{ width: "300px", height: "300px" }}
                titleText={text.EnterYourCid}
                subtitleText={text.EnterYourCidDescription}
                submitText={text.SendCid}
            />
        </DigitLayout.Center>
    );
};

export default InputCid;
