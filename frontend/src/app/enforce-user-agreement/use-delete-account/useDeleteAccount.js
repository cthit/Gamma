import {
    DigitButton,
    DigitEditData,
    DigitTextField,
    useDigitCustomDialog,
    useDigitTranslations,
    DigitLayout
} from "@cthit/react-digit-components";
import * as yup from "yup";
import { deleteMe } from "../../../api/me/delete.me.api";
import { getBackendUrl } from "../../../common/utils/configs/envVariablesLoader";

const translations = {
    Password: ["Your password", "Ditt lösenord"],
    AreYouReallySure: [
        "Are you really sure you want to permanently delete your own account. This action is irreversibel.",
        "Är du säker att du verkligen vill radera ditt konto permanent? Denna återgärd är oåterkallelig."
    ],
    YouMustEnterPassword: [
        "You need to confirm your password to be able to delete your account",
        "Du måste bekräfta ditt lösenord för att kunna radera ditt konto"
    ],
    DeleteMe: ["Delete my account", "Radera mitt konto"]
};

function useDeleteAccount() {
    const [text] = useDigitTranslations(translations);
    const [openCustomDialog] = useDigitCustomDialog();

    const startDeleteAccountProcess = () => {
        openCustomDialog({
            title: text.AreYouReallySure,
            renderMain: () => (
                <DigitEditData
                    centerFields
                    keysComponentData={{
                        password: {
                            component: DigitTextField,
                            componentProps: {
                                upperLabel: text.Password,
                                password: true,
                                outlined: true,
                                autoComplete: false
                            }
                        }
                    }}
                    onSubmit={passwordData =>
                        deleteMe(passwordData).then(() => {
                            window.location.replace(
                                getBackendUrl() + "/account-deleted"
                            );
                        })
                    }
                    keysOrder={["password"]}
                    submitText={text.DeleteMe}
                    validationSchema={yup.object().shape({
                        password: yup
                            .string()
                            .min(8)
                            .required(text.YouMustEnterPassword)
                    })}
                    initialValues={{ password: "" }}
                    formName={"delete-me"}
                />
            ),
            renderButtons: (confirm, cancel) => (
                <div
                    style={{
                        width: "100%",
                        display: "flex",
                        flexDirection: "row",
                        justifyContent: "space-between"
                    }}
                >
                    <DigitButton onClick={cancel} outlined text={text.Cancel} />
                    <DigitButton
                        onClick={confirm}
                        outlined
                        text={text.DeleteMe}
                        submit
                        form={"delete-me"}
                    />
                </div>
            )
        });
    };

    return startDeleteAccountProcess;
}

export default useDeleteAccount;
