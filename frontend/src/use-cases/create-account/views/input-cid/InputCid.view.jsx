import {
    useDigitTranslations,
    DigitEditDataCard,
    DigitLayout,
    DigitDesign,
    DigitSelect,
    useDigitToast
} from "@cthit/react-digit-components";
import React from "react";
import translations from "./InputCid.view.translations";
import { useHistory } from "react-router-dom";
import { activateCid } from "../../../../api/whitelist/post.whitelist.api";
import {
    initialValues,
    keysComponentData,
    keysOrder,
    validationSchema
} from "./InputCid.view.options";

const InputCid = () => {
    const [queueToast] = useDigitToast();
    const [text, activeLanguage, setActiveLanguage] = useDigitTranslations(
        translations
    );
    const history = useHistory();

    return (
        <DigitLayout.Center>
            <DigitDesign.Card margin={{ bottom: "16px" }}>
                <DigitDesign.CardHeader>
                    <DigitDesign.CardTitle text={text.ChooseLanguage} />
                </DigitDesign.CardHeader>
                <DigitDesign.CardBody>
                    <DigitSelect
                        alignSelf={"center"}
                        value={activeLanguage}
                        onChange={e => {
                            setActiveLanguage(e.target.value);
                        }}
                        valueToTextMap={{ sv: text.Swedish, en: text.English }}
                        outlined
                        upperLabel={text.YourLanguage}
                    />
                </DigitDesign.CardBody>
            </DigitDesign.Card>
            <DigitEditDataCard
                validationSchema={validationSchema(text)}
                initialValues={initialValues()}
                keysComponentData={keysComponentData(text)}
                keysOrder={keysOrder()}
                centerFields
                onSubmit={(values, actions) => {
                    activateCid(values)
                        .then(() => {
                            actions.resetForm();
                            actions.setSubmitting(false);
                            history.push("/create-account/email-sent");
                        })
                        .catch(() => {
                            queueToast({
                                text: text.SomethingWentWrong,
                                duration: 3000
                            });
                        });
                }}
                size={{ width: "400px", height: "300px" }}
                titleText={text.EnterYourCid}
                subtitleText={text.EnterYourCidDescription}
                submitText={text.SendCid}
                extraButton={{
                    text: text.AlreadyHaveCode
                }}
                extraButtonTo={"/create-account/email-sent"}
            />
        </DigitLayout.Center>
    );
};

export default InputCid;
