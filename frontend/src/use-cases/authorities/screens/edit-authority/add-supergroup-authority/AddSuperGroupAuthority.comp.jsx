import React from "react";
import {
    DigitEditDataCard,
    DigitSelect,
    useDigitToast,
    useDigitTranslations
} from "@cthit/react-digit-components";
import * as yup from "yup";
import { addSuperGroupToAuthorityLevel } from "../../../../../api/authorities/post.authoritites";
import translations from "../EditAuthority.screen.translations.json";

const AddSuperGroupAuthority = ({
    superGroupMap,
    setRead,
    authorityLevelName
}) => {
    const [text] = useDigitTranslations(translations);
    const [queueToast] = useDigitToast();

    return (
        <DigitEditDataCard
            margin={"0px"}
            size={{ width: "100%", maxWidth: "400px" }}
            alignSelf={"center"}
            centerFields
            submitText={text.Add}
            validationSchema={yup.object().shape({
                superGroup: yup
                    .string()
                    .required(text.SuperGroup + text.IsRequired)
            })}
            onSubmit={(values, actions) =>
                addSuperGroupToAuthorityLevel({
                    superGroupId: values.superGroup,
                    authorityLevelName
                })
                    .then(() => {
                        setRead(true);
                        actions.resetForm();
                        queueToast({
                            text: text.AddedToAuthorityLevel
                        });
                    })
                    .catch(() => {
                        queueToast({
                            text: text.FailedToAuthorityLevel
                        });
                    })
            }
            keysOrder={["superGroup"]}
            keysComponentData={{
                superGroup: {
                    component: DigitSelect,
                    componentProps: {
                        upperLabel: text.SuperGroup,
                        valueToTextMap: superGroupMap,
                        outlined: true
                    }
                }
            }}
        />
    );
};

export default AddSuperGroupAuthority;
