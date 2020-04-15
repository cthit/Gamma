import {
    DigitAvatar,
    DigitLayout,
    DigitText,
    DigitMenu,
    DigitNavLink,
    useDigitTranslations
} from "@cthit/react-digit-components";
import React from "react";
import translations from "./Drawer.element.translations";
import useGammaUser from "../../../common/hooks/use-gamma-user/useGammaUser";
import useGammaIs from "../../../common/hooks/use-gamma-is/use-gamma-is";
import useGammaIsAdmin from "../../../common/hooks/use-gamma-is-admin/useGammaIsAdmin";
import { getBackendUrl } from "../../../common/utils/configs/envVariablesLoader";
import ExpandMore from "@material-ui/icons/ExpandMore";

const GammaActions = ({ text }) => {
    const user = useGammaUser();

    return (
        <DigitLayout.Row
            justifyContent={"center"}
            alignItems={"center"}
            size={{
                width: "100%",
                height: "60px",
                minHeight: "60px",
                maxHeight: "60px"
            }}
        >
            <DigitAvatar
                imageAlt={"Avatar"}
                imageSrc={user.avatarUrl}
                margin={{ right: "16px" }}
            />
            <DigitText.Title text={user.nick} />
            <DigitMenu
                icon={ExpandMore}
                onClick={item => {
                    switch (item) {
                        case "signOut":
                            window.location.href = getBackendUrl() + "/logout";
                            break;
                        default:
                            break;
                    }
                }}
                valueToTextMap={{
                    signOut: text.SignOut
                }}
                order={["signOut"]}
            />
        </DigitLayout.Row>
    );
};

const Drawer = ({ closeDrawer }) => {
    const user = useGammaUser();
    const admin = useGammaIsAdmin();
    const dpo = useGammaIs("gdpr");
    const [text] = useDigitTranslations(translations);

    if (admin) {
        return (
            <DigitLayout.Column padding="0" margin="0">
                {user != null && <GammaActions text={text} />}

                <DigitNavLink onClick={closeDrawer} text={text.Home} link="/" />
                <DigitNavLink onClick={closeDrawer} text={text.Me} link="/me" />
                <DigitNavLink
                    onClick={closeDrawer}
                    text={text.Users}
                    link="/users"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text={text.Groups}
                    link="/groups"
                />
                <DigitNavLink
                    onClose={closeDrawer}
                    text={text.SuperGroups}
                    link="/super-groups"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text={text.Posts}
                    link="/posts"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text={text.Whitelist}
                    link="/whitelist"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text={text.GDPR}
                    link="/gdpr"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text={text.ActivationCodes}
                    link="/activation-codes"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text={text.Clients}
                    link="/clients"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text={text.ApiKeys}
                    link="/access-keys"
                />
            </DigitLayout.Column>
        );
    } else {
        return (
            <DigitLayout.Column padding="0">
                <DigitNavLink onClick={closeDrawer} text="Home" link="/" />
                <DigitNavLink onClick={closeDrawer} text="Me" link="/me" />
                <DigitNavLink
                    onClick={closeDrawer}
                    text="Users"
                    link="/users"
                />
                <DigitNavLink
                    onClick={closeDrawer}
                    text="Groups"
                    link="/groups"
                />
                <DigitNavLink
                    onClose={closeDrawer}
                    text={text.SuperGroups}
                    link="/super-groups"
                />
                {dpo && (
                    <DigitNavLink
                        onClick={closeDrawer}
                        text={text.GDPR}
                        link="/gdpr"
                    />
                )}
            </DigitLayout.Column>
        );
    }
};

export default Drawer;
