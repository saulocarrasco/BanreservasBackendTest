package com.banreservas.customer.application;

import com.banreservas.customer.application.command.CreateCustomerCommand;
import com.banreservas.customer.application.command.UpdateCustomerCommand;
import com.banreservas.customer.domain.exception.CountryServiceUnavailableException;
import com.banreservas.customer.domain.exception.CustomerNotFoundException;
import com.banreservas.customer.domain.exception.DuplicateEmailException;
import com.banreservas.customer.domain.exception.InvalidCountryException;
import com.banreservas.customer.domain.model.Customer;
import com.banreservas.customer.domain.model.PagedResult;
import com.banreservas.customer.domain.port.CountryGateway;
import com.banreservas.customer.domain.port.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    CustomerRepository customerRepository;

    @Mock
    CountryGateway countryGateway;

    @InjectMocks
    CustomerService customerService;

    private Customer sampleCustomer;

    @BeforeEach
    void setUp() {
        sampleCustomer = new Customer(1L, "Juan", null, "Perez", null,
            "juan@example.com", "Calle 1", "+1-809-555-0100", "DO", "Dominican",
            LocalDateTime.now(), LocalDateTime.now());
    }

    // --- create ---

    @Test
    void create_givenValidRequest_returnsSavedCustomer() {
        CreateCustomerCommand cmd = validCreateCommand("juan@example.com", "DO");
        when(customerRepository.existsByEmail("juan@example.com")).thenReturn(false);
        when(countryGateway.getDemonym("DO")).thenReturn("Dominican");
        when(customerRepository.save(any())).thenReturn(sampleCustomer);

        Customer result = customerService.create(cmd);

        assertThat(result.firstName()).isEqualTo("Juan");
        assertThat(result.demonym()).isEqualTo("Dominican");
        verify(countryGateway).getDemonym("DO");
        verify(customerRepository).save(any());
    }

    @Test
    void create_normalizesEmailAndCountry() {
        CreateCustomerCommand cmd = validCreateCommand("  Juan@EXAMPLE.COM  ", "  do  ");
        when(customerRepository.existsByEmail("juan@example.com")).thenReturn(false);
        when(countryGateway.getDemonym("DO")).thenReturn("Dominican");
        when(customerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        customerService.create(cmd);

        verify(customerRepository).existsByEmail("juan@example.com");
        verify(countryGateway).getDemonym("DO");
    }

    @Test
    void create_givenDuplicateEmail_throwsDuplicateEmailException() {
        CreateCustomerCommand cmd = validCreateCommand("existing@example.com", "DO");
        when(customerRepository.existsByEmail("existing@example.com")).thenReturn(true);

        assertThatThrownBy(() -> customerService.create(cmd))
            .isInstanceOf(DuplicateEmailException.class);
        verify(countryGateway, never()).getDemonym(anyString());
    }

    @Test
    void create_givenInvalidCountry_throwsInvalidCountryException() {
        CreateCustomerCommand cmd = validCreateCommand("juan@example.com", "XX");
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(countryGateway.getDemonym("XX")).thenThrow(new InvalidCountryException("XX"));

        assertThatThrownBy(() -> customerService.create(cmd))
            .isInstanceOf(InvalidCountryException.class);
    }

    @Test
    void create_givenApiDown_throwsCountryServiceUnavailableException() {
        CreateCustomerCommand cmd = validCreateCommand("juan@example.com", "DO");
        when(customerRepository.existsByEmail(anyString())).thenReturn(false);
        when(countryGateway.getDemonym("DO")).thenThrow(new CountryServiceUnavailableException());

        assertThatThrownBy(() -> customerService.create(cmd))
            .isInstanceOf(CountryServiceUnavailableException.class);
    }

    // --- findAll ---

    @Test
    void findAll_returnsAllCustomers() {
        when(customerRepository.findAll()).thenReturn(List.of(sampleCustomer));

        List<Customer> result = customerService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
    }

    // --- findByCountry ---

    @Test
    void findByCountry_normalizedUppercase_delegatesToRepository() {
        when(customerRepository.findByCountry("DO")).thenReturn(List.of(sampleCustomer));

        List<Customer> result = customerService.findByCountry("do");

        assertThat(result).hasSize(1);
        verify(customerRepository).findByCountry("DO");
    }

    // --- findAllPaged ---

    @Test
    void findAllPaged_returnsPagedResult() {
        PagedResult<Customer> paged = new PagedResult<>(List.of(sampleCustomer), 0, 10, 1L, 1);
        when(customerRepository.findAllPaged(0, 10, null)).thenReturn(paged);

        PagedResult<Customer> result = customerService.findAllPaged(0, 10, null);

        assertThat(result.total()).isEqualTo(1L);
        assertThat(result.data()).hasSize(1);
    }

    // --- findById ---

    @Test
    void findById_givenExistingId_returnsCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer));

        Customer result = customerService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
    }

    @Test
    void findById_givenNonExistentId_throwsCustomerNotFoundException() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findById(999L))
            .isInstanceOf(CustomerNotFoundException.class);
    }

    // --- update ---

    @Test
    void update_givenPartialUpdate_appliesOnlyProvidedFields() {
        UpdateCustomerCommand cmd = new UpdateCustomerCommand(null, "New Address", null, null);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer));
        when(customerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Customer result = customerService.update(1L, cmd);

        assertThat(result.address()).isEqualTo("New Address");
        assertThat(result.email()).isEqualTo("juan@example.com");
        verify(countryGateway, never()).getDemonym(anyString());
    }

    @Test
    void update_givenCountryChange_refetchesDemonym() {
        UpdateCustomerCommand cmd = new UpdateCustomerCommand(null, null, null, "US");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer));
        when(countryGateway.getDemonym("US")).thenReturn("American");
        when(customerRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Customer result = customerService.update(1L, cmd);

        assertThat(result.country()).isEqualTo("US");
        assertThat(result.demonym()).isEqualTo("American");
        verify(countryGateway).getDemonym("US");
    }

    @Test
    void update_givenDuplicateEmailOnUpdate_throwsDuplicateEmailException() {
        UpdateCustomerCommand cmd = new UpdateCustomerCommand("taken@example.com", null, null, null);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer));
        when(customerRepository.existsByEmail("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> customerService.update(1L, cmd))
            .isInstanceOf(DuplicateEmailException.class);
    }

    @Test
    void update_givenNonExistentId_throwsCustomerNotFoundException() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.update(999L, new UpdateCustomerCommand(null, null, null, null)))
            .isInstanceOf(CustomerNotFoundException.class);
    }

    // --- delete ---

    @Test
    void delete_givenExistingId_deletesCustomer() {
        when(customerRepository.findById(1L)).thenReturn(Optional.of(sampleCustomer));

        customerService.delete(1L);

        verify(customerRepository).deleteById(1L);
    }

    @Test
    void delete_givenNonExistentId_throwsCustomerNotFoundException() {
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.delete(999L))
            .isInstanceOf(CustomerNotFoundException.class);
        verify(customerRepository, never()).deleteById(anyLong());
    }

    // --- helpers ---

    private CreateCustomerCommand validCreateCommand(String email, String country) {
        return new CreateCustomerCommand("Juan", null, "Perez", null, email, "Calle 1", "+1-809-555-0100", country);
    }
}
