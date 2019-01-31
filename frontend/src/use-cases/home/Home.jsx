import React from "react";

import { DigitLayout } from "@cthit/react-digit-components";

import UserOptions from "./views/user-options";
import AdminOptions from "./views/admin-options";
import WelcomeUser from "./elements/welcome-user";

class Home extends React.Component {
    componentDidMount() {
        this.props.gammaLoadingFinished();
    }

    render() {
        const { user } = this.props;

        return (
            <DigitLayout.Center>
                <DigitLayout.Column>
                    <WelcomeUser user={user} />
                    <DigitLayout.Spacing />
                    <UserOptions />
                    <DigitLayout.Spacing />
                    <AdminOptions />
                </DigitLayout.Column>
            </DigitLayout.Center>
        );
    }
}

export default Home;
