package Musicfy.MusicfyOrigin.Product.Service;

import Musicfy.MusicfyOrigin.Product.dto.EnderecoDTO;
import Musicfy.MusicfyOrigin.Product.dto.OrderDTO;
import Musicfy.MusicfyOrigin.Product.dto.OrderItemDTO;
import Musicfy.MusicfyOrigin.Product.model.Order;
import Musicfy.MusicfyOrigin.Product.model.OrderItem;
import Musicfy.MusicfyOrigin.Product.model.OrderStatus;
import Musicfy.MusicfyOrigin.Product.repository.OrderRepository;
import Musicfy.MusicfyOrigin.Product.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final UsuarioRepository usuarioRepository;

    public OrderService(OrderRepository orderRepository, UsuarioRepository usuarioRepository) {
        this.orderRepository = orderRepository;
        this.usuarioRepository = usuarioRepository;
    }

    public List<OrderDTO> getUserOrders(String firebaseUid) {
        List<Order> orders = orderRepository.findByUser_FirebaseUidOrderByOrderDateDesc(firebaseUid);
        return orders.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public OrderDTO getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + orderId));
        return convertToDTO(order);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Pedido não encontrado com ID: " + orderId));

        // REMOVA: if (newStatus == OrderStatus.CANCELLED) { orderRepository.delete(order); return null; }
        // Em vez de deletar, simplesmente atualize o status para CANCELLED (ou qualquer outro status)
        order.setStatus(newStatus); // Esta linha agora será executada para todos os status, incluindo CANCELLED
        orderRepository.save(order);

        return convertToDTO(order);
    }

    private OrderDTO convertToDTO(Order order) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setUserId(order.getUser().getFirebaseUid());
        dto.setOrderDate(order.getOrderDate().format(formatter));
        dto.setStatus(order.getStatus());
        dto.setTotalPrice(order.getTotalPrice());

        if (order.getDeliveryAddress() != null) {
            dto.setDeliveryAddress(new EnderecoDTO(
                    order.getDeliveryAddress().getId(),
                    order.getDeliveryAddress().getCep(),
                    order.getDeliveryAddress().getBairro(),
                    order.getDeliveryAddress().getCidade(),
                    order.getDeliveryAddress().getEstado(),
                    order.getDeliveryAddress().getComplemento(),
                    order.getDeliveryAddress().getRua(),
                    order.getDeliveryAddress().getTipo()
            ));
        }

        dto.setItems(order.getItems().stream()
                .map(this::convertOrderItemToDTO)
                .collect(Collectors.toList()));

        return dto;
    }

    private OrderItemDTO convertOrderItemToDTO(OrderItem orderItem) {
        OrderItemDTO itemDTO = new OrderItemDTO();
        itemDTO.setProductId(orderItem.getProduct().getId());
        itemDTO.setProductName(orderItem.getProduct().getName());
        // Ajuste aqui para o getter correto do caminho da imagem
        itemDTO.setProductImgPath(orderItem.getProduct().getImgPath());
        itemDTO.setQuantity(orderItem.getQuantity());
        itemDTO.setUnitPrice(orderItem.getUnitPriceAtPurchase()); // ou getUnitPrice(), conforme seu modelo
        itemDTO.setTotalPrice(orderItem.getUnitPriceAtPurchase() * orderItem.getQuantity());
        return itemDTO;
    }
}